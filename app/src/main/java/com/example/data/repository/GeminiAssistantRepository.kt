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

        // شناسه‌های رسمی و متمرکز مدل‌های فعال Gemini
        const val MODEL_FAST = "gemini-3.5-flash"
        const val MODEL_REASONING = "gemini-3.5-flash"

        // لیست مدل‌های جایگزین به ترتیب اولویت (حداکثر ۳ مدل)
        val FALLBACK_MODELS = listOf(
            "gemini-3.5-flash",
            "gemini-3.5-flash-lite",
            "gemini-3.1-flash-lite"
        )

        private val SYSTEM_INSTRUCTION = GeminiContent(
            parts = listOf(
                GeminiPart(
                    "تو «دستیار برسام» هستی، دستیار هوشمند و دلسوز آموزشی درس ریاضی برای دانش‌آموزان پایه‌های دهم، یازدهم و دوازدهم در برنامه «برسام و ریاضی». " +
                            "وظیفه تو راهنمایی گام‌به‌گام در حل مسائل، آموزش مفهومی فرمول‌ها، و تفکیک مراحل محاسباتی است. " +
                            "پاسخ‌ها را به زبان فارسی، روان، دقیق و ساختاریافته ارائه بده. ابتدا ایده اصلی حل را بگو و سپس گام‌های تفصیلی را توضیح بده. " +
                            "از Markdown و LaTeX استفاده نکن؛ فرمول‌ها را با نمادهای ساده مثل √ ، ² ، π ، ∞ و متن معمولی بنویس. " +
                            "اگر صورت مسئله ناقص است، ابتدا سوال روشن‌کننده بپرس. تا حد امکان از اعداد فارسی برای متن‌های عادی استفاده کن، ولی در فرمول‌ها ارقام لاتین بمانند."
                )
            )
        )

        fun createDefaultApiService(): GeminiApiService {
            val okHttpClient = OkHttpClient.Builder()
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(45, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
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
     * مکانیزم Fallback ترتیبی بین ۳ مدل سریع با مدیریت دقیق Timeout و خطاهای HTTP
     */
    suspend fun askMathAssistant(
        userQuestion: String,
        isComplexMath: Boolean = false,
        onStatusUpdate: (String) -> Unit = {}
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

        // ۴. پیکربندی دما و حذف کامل thinkingConfig (عدم ارسال thinkingLevel)
        val generationConfig = GeminiGenerationConfig(
            temperature = if (isComplexMath) 0.4f else 0.7f,
            thinkingConfig = null
        )

        val request = GeminiRequest(
            contents = listOf(
                GeminiContent(
                    parts = listOf(GeminiPart(userQuestion))
                )
            ),
            systemInstruction = SYSTEM_INSTRUCTION,
            generationConfig = generationConfig
        )

        // ۵. تلاش ترتیبی بین حداکثر ۳ مدل
        val modelsToTry = FALLBACK_MODELS
        var lastError: AiAssistantResult.Error? = null

        for (index in modelsToTry.indices) {
            val currentModel = modelsToTry[index]
            if (index > 0) {
                onStatusUpdate("مدل اول دیر جواب داد، مدل سریعتر را امتحان میکنم...")
            }

            try {
                val response = apiService.generateContent(currentModel, apiKey, request)
                if (response.isSuccessful) {
                    val body = response.body()
                    val replyText = body?.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                    if (!replyText.isNullOrBlank()) {
                        return@withContext AiAssistantResult.Success(replyText.trim(), currentModel)
                    } else {
                        lastError = AiAssistantResult.Error(
                            "پاسخی از دستیار دریافت نشد. لطفاً پرسش خود را دوباره بیان کنید.",
                            "Empty - $currentModel"
                        )
                        continue
                    }
                } else {
                    val code = response.code()
                    val technicalDetail = "HTTP $code - $currentModel"

                    // روی ۴۰۰ و ۴۰۱ به هیچ عنوان مدل بعدی امتحان نمی‌شود
                    if (code == 400 || code == 401) {
                        val userMsg = if (code == 400) {
                            "صورت مسئله ارسالی نیاز به شفاف‌سازی دارد (کد ۴۰۰)."
                        } else {
                            "خطای احراز هویت در دسترسی به Gemini API با کد ۴۰۱. لطفاً کلید API را بررسی فرمایید."
                        }
                        return@withContext AiAssistantResult.Error(userMsg, technicalDetail)
                    }

                    // بررسی کدهای مجاز برای رفتن به مدل بعدی: ۴۰۴، ۴۰۳، ۵۰۰، ۵۰۲، ۵۰۳، ۴۲۹
                    val isFallbackCode = code in listOf(404, 403, 500, 502, 503, 429)
                    val userMsg = when (code) {
                        403 -> "خطای دسترسی به Gemini API با کد ۴۰۳. لطفاً کلید API یا دسترسی منطقه را بررسی فرمایید."
                        404 -> "مدل هوش مصنوعی انتخابی ($currentModel) در دسترس نیست یا یافت نشد (کد ۴۰۴)."
                        429 -> "سقف مجاز درخواست‌های لحظه‌ای پر شده است (کد ۴۲۹). لطفاً چند لحظه بعد تلاش کنید."
                        500, 502, 503 -> "سرویس هوش مصنوعی در حال حاضر با مشکل مواجه است (کد $code). لطفاً دقایقی دیگر تلاش فرمایید."
                        else -> "خطایی در دریافت پاسخ از سرویس با کد $code رخ داد."
                    }
                    lastError = AiAssistantResult.Error(userMsg, technicalDetail)

                    if (!isFallbackCode) {
                        return@withContext lastError
                    }
                }
            } catch (e: CancellationException) {
                // حفظ ساختار لغو کوروتین و پرتاب مجدد استثنا
                throw e
            } catch (e: SocketTimeoutException) {
                val isConnectTimeout = e.message?.contains("connect", ignoreCase = true) == true
                lastError = if (isConnectTimeout) {
                    AiAssistantResult.Error(
                        "به سرور هوش مصنوعی وصل نشد. اینترنت یا VPN را بررسی کن.",
                        "Connect Timeout - $currentModel"
                    )
                } else {
                    AiAssistantResult.Error(
                        "مدل دیر جواب داد. دوباره امتحان کن.",
                        "Timeout"
                    )
                }
                // تایماوت مجاز به رفتن به مدل بعدی است
                continue
            } catch (e: UnknownHostException) {
                // عدم اتصال: به سرور وصل نشد
                return@withContext AiAssistantResult.Error(
                    "به سرور هوش مصنوعی وصل نشد. اینترنت یا VPN را بررسی کن.",
                    "UnknownHost"
                )
            } catch (e: java.net.ConnectException) {
                // عدم اتصال: به سرور وصل نشد
                return@withContext AiAssistantResult.Error(
                    "به سرور هوش مصنوعی وصل نشد. اینترنت یا VPN را بررسی کن.",
                    "ConnectException"
                )
            } catch (e: Exception) {
                lastError = AiAssistantResult.Error(
                    "خطایی در برقراری ارتباط با دستیار رخ داد: ${e.localizedMessage ?: "نامشخص"}",
                    "Exception: ${e.javaClass.simpleName} - $currentModel"
                )
            }
        }

        return@withContext lastError ?: AiAssistantResult.Error(
            "خطایی در دریافت پاسخ از سرویس رخ داد. لطفاً دوباره تلاش کنید."
        )
    }
}
