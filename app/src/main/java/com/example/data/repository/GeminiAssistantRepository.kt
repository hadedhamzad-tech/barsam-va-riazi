package com.example.data.repository

import android.content.Context
import com.example.BuildConfig
import com.example.data.local.dao.SettingsDao
import com.example.data.remote.AiAssistantResult
import com.example.data.remote.AndroidNetworkChecker
import com.example.data.remote.GeminiApiService
import com.example.data.remote.GeminiContent
import com.example.data.remote.GeminiGenerationConfig
import com.example.data.remote.GeminiPart
import com.example.data.remote.GeminiRequest
import com.example.data.remote.GeminiThinkingConfig
import com.example.data.remote.NetworkChecker
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.TimeUnit

/**
 * مخزن مدیریت دستیار هوشمند درسی برسام با انطباق کامل آفلاین/آنلاین
 * - احراز هویت امن از طریق هدر x-goog-api-key
 * - حفظ ساختار لغو Coroutine با ریتنشن CancellationException
 * - وابستگی تست‌پذیر به NetworkChecker و apiKeyProvider
 */
class GeminiAssistantRepository(
    private val context: Context,
    private val settingsDao: SettingsDao,
    private val apiService: GeminiApiService = createDefaultApiService(),
    private val networkChecker: NetworkChecker = AndroidNetworkChecker(context),
    private val apiKeyProvider: () -> String = { BuildConfig.GEMINI_API_KEY }
) {

    companion object {
        private const val BASE_URL = "https://generativelanguage.googleapis.com/"

        // شناسه‌های رسمی و معتبر مدل‌های فعال Gemini در ai.google.dev:
        // ۱. مدل سریع برای پرسش‌های متداول و خلاصه‌سازی
        const val MODEL_FAST = "gemini-2.5-flash"
        // ۲. مدل استدلال عمیق با توانایی حل گام‌به‌گام و تحلیلی مسائل ریاضی کنکور
        const val MODEL_REASONING = "gemini-2.5-pro"

        private val SYSTEM_INSTRUCTION = GeminiContent(
            parts = listOf(
                GeminiPart(
                    "تو «دستیار برسام» هستی، دستیار هوشمند و دلسوز آموزشی درس ریاضی برای دانش‌آموزان پایه‌های دهم، یازدهم و دوازدهم در برنامه «برسام و ریاضی». " +
                            "وظیفه تو راهنمایی گام‌به‌گام در حل مسائل، آموزش مفهومی فرمول‌ها، و تفکیک مراحل محاسباتی است. " +
                            "پاسخ‌ها را به زبان فارسی، روان، دقیق و ساختاریافته ارائه بده. ابتدا ایده اصلی حل را بگو و سپس گام‌های تفصیلی را توضیح بده. " +
                            "اگر صورت مسئله ناقص است، ابتدا سوال روشن‌کننده بپرس. تا حد امکان از اعداد فارسی استفاده کن."
                )
            )
        )

        fun createDefaultApiService(): GeminiApiService {
            val okHttpClient = OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build()

            val moshi = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()

            return retrofit.create(GeminiApiService::class.java)
        }
    }

    /**
     * وضعیت روشن یا خاموش بودن دستیار هوش مصنوعی به صورت Flow
     */
    fun isAiEnabledFlow(): Flow<Boolean> {
        return settingsDao.getSettings().map { it?.isAiEnabled ?: false }
    }

    /**
     * تغییر وضعیت سوییچ هوش مصنوعی در تنظیمات پایدار
     */
    suspend fun setAiEnabled(enabled: Boolean) {
        settingsDao.setAiEnabled(enabled)
    }

    /**
     * ارسال پرسش به دستیار هوشمند برسام
     * به همراه تفکیک کامل لایه‌های آفلاین و آنلاین و جلوگیری قطعی از ارسال درخواست در حالت خاموش
     */
    suspend fun askMathAssistant(
        userQuestion: String,
        isComplexMath: Boolean = false
    ): AiAssistantResult = withContext(Dispatchers.IO) {
        // ۱. بررسی وضعیت سوییچ تنظیمات: در صورت خاموش بودن، هیچ درخواستی به شبکه ارسال نمی‌شود
        val settings = settingsDao.getSettingsDirect()
        val isEnabled = settings?.isAiEnabled ?: false
        if (!isEnabled) {
            return@withContext AiAssistantResult.Disabled(
                "دستیار هوش مصنوعی در تنظیمات غیرفعال است. جهت دریافت راهنمایی تشریحی، آن را از بخش تنظیمات فعال کنید."
            )
        }

        // ۲. بررسی اتصال اینترنت: در نبود اتصال، پیام مناسب آفلاین برمی‌گرداند
        if (!networkChecker.isNetworkAvailable()) {
            return@withContext AiAssistantResult.Offline(
                "اتصال به اینترنت برقرار نیست. لطفاً وضعیت شبکه خود را بررسی کنید. (امکانات تمرین و آزمون آفلاین همچنان فعال هستند)"
            )
        }

        // ۳. بررسی وجود کلید API معتبر
        val apiKey = apiKeyProvider()
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext AiAssistantResult.Error(
                "کلید هوش مصنوعی در برنامه تنظیم نشده است. لطفاً کلید معتبر Gemini را در پنل Secrets پلتفرم وارد فرمایید."
            )
        }

        // ۴. انتخاب مدل و پیکربندی بر اساس پیچیدگی مسئله
        val model = if (isComplexMath) MODEL_REASONING else MODEL_FAST
        val generationConfig = if (isComplexMath) {
            GeminiGenerationConfig(
                temperature = 0.4f,
                thinkingConfig = GeminiThinkingConfig(thinkingLevel = "HIGH")
            )
        } else {
            GeminiGenerationConfig(temperature = 0.7f)
        }

        val request = GeminiRequest(
            contents = listOf(
                GeminiContent(
                    parts = listOf(GeminiPart(userQuestion))
                )
            ),
            systemInstruction = SYSTEM_INSTRUCTION,
            generationConfig = generationConfig
        )

        // ۵. فراخوانی امن و مدیریت جامع خطاها
        try {
            // کلید به عنوان هدر x-goog-api-key ارسال می‌شود
            val response = apiService.generateContent(model, apiKey, request)
            if (response.isSuccessful) {
                val body = response.body()
                val replyText = body?.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                if (!replyText.isNullOrBlank()) {
                    AiAssistantResult.Success(replyText.trim(), model)
                } else {
                    AiAssistantResult.Error("پاسخی از دستیار دریافت نشد. لطفاً پرسش خود را دوباره بیان کنید.")
                }
            } else {
                val code = response.code()
                val userMsg = when (code) {
                    400 -> "صورت مسئله ارسالی نیاز به شفاف‌سازی دارد. لطفاً با جزئیات بیشتر بنویسید."
                    401, 403 -> "خطای احراز هویت در دسترسی به Gemini API. لطفاً از صحت کلید اطمینان یابید."
                    404 -> "مدل هوش مصنوعی انتخابی ($model) در دسترس نیست یا یافت نشد (کد ۴۰۴). لطفاً مدل دیگری را انتخاب فرمایید."
                    429 -> "سقف مجاز درخواست‌های لحظه‌ای پر شده است. لطفاً چند لحظه بعد تلاش کنید."
                    500, 503 -> "سرویس هوش مصنوعی در حال حاضر با بار کاری بالا مواجه است. لطفاً دقایقی دیگر تلاش فرمایید."
                    else -> "خطایی در دریافت پاسخ از سرویس با کد $code رخ داد."
                }
                AiAssistantResult.Error(userMsg, "HTTP $code")
            }
        } catch (e: CancellationException) {
            // حفظ ساختار لغو کوروتین و پرتاب مجدد استثنا
            throw e
        } catch (e: SocketTimeoutException) {
            AiAssistantResult.Error(
                "زمان انتظار برای دریافت پاسخ به پایان رسید (Timeout). سرعت اینترنت خود را بررسی کنید.",
                "Timeout"
            )
        } catch (e: UnknownHostException) {
            AiAssistantResult.Offline(
                "امکان اتصال به سرور هوش مصنوعی وجود ندارد. لطفاً ارتباط اینترنت خود را بررسی کنید."
            )
        } catch (e: Exception) {
            // بدون افشای کلید یا اطلاعات محرمانه در پیام
            AiAssistantResult.Error(
                "خطایی در برقراری ارتباط با دستیار رخ داد: ${e.localizedMessage ?: "نامشخص"}"
            )
        }
    }
}
