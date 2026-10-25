package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.BarsamDatabase
import com.example.data.local.entity.AppSettingsEntity
import com.example.data.remote.AiAssistantResult
import com.example.data.remote.GeminiApiService
import com.example.data.remote.GeminiCandidate
import com.example.data.remote.GeminiContent
import com.example.data.remote.GeminiPart
import com.example.data.remote.GeminiRequest
import com.example.data.remote.GeminiResponse
import com.example.data.remote.NetworkChecker
import com.example.data.repository.GeminiAssistantRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import retrofit2.Response
import java.net.SocketTimeoutException

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class BarsamPhase3Test {

    private lateinit var database: BarsamDatabase
    private lateinit var context: Context

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        database = Room.inMemoryDatabaseBuilder(context, BarsamDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun tearDown() {
        database.close()
    }

    /**
     * پیاده‌سازی کنترل‌شده آزمون اینترفیس Retrofit با بررسی هدر احراز هویت و پارامترها
     */
    private class MockGeminiApiService(
        var shouldSucceed: Boolean = true,
        var shouldTimeout: Boolean = false,
        var shouldThrowCancellation: Boolean = false,
        var errorCode: Int = 200,
        var responseText: String = "راه حل مرحله به مرحله: x = 2",
        var customHandler: ((model: String, callCount: Int, request: GeminiRequest) -> Response<GeminiResponse>)? = null
    ) : GeminiApiService {
        var callCount: Int = 0
        var lastModelCalled: String? = null
        var lastApiKeyReceived: String? = null
        var lastRequestReceived: GeminiRequest? = null
        val modelsCalled = mutableListOf<String>()

        override suspend fun generateContent(
            model: String,
            apiKey: String,
            request: GeminiRequest
        ): Response<GeminiResponse> {
            callCount++
            lastModelCalled = model
            modelsCalled.add(model)
            lastApiKeyReceived = apiKey
            lastRequestReceived = request

            if (customHandler != null) {
                return customHandler!!(model, callCount, request)
            }

            if (shouldThrowCancellation) {
                throw CancellationException("Simulated job cancellation")
            }

            if (shouldTimeout) {
                throw SocketTimeoutException("Simulated socket timeout")
            }

            return if (shouldSucceed) {
                Response.success(
                    GeminiResponse(
                        candidates = listOf(
                            GeminiCandidate(
                                content = GeminiContent(
                                    parts = listOf(GeminiPart(responseText))
                                )
                            )
                        )
                    )
                )
            } else {
                Response.error(
                    errorCode,
                    "{\"error\":\"Simulated error\"}".toResponseBody("application/json".toMediaTypeOrNull())
                )
            }
        }

        override suspend fun listModels(apiKey: String): Response<com.example.data.remote.GeminiModelListResponse> {
            return Response.success(com.example.data.remote.GeminiModelListResponse(emptyList()))
        }
    }

    // ۱. بررسی عدم ارسال هرگونه درخواست در حالت خاموش
    @Test
    fun `verify zero network requests when AI switch is OFF in settings`() = runBlocking {
        val settingsDao = database.settingsDao()
        settingsDao.saveSettings(AppSettingsEntity(id = 1, isAiEnabled = false))

        val mockApi = MockGeminiApiService()
        val repository = GeminiAssistantRepository(
            context = context,
            settingsDao = settingsDao,
            apiService = mockApi,
            networkChecker = { true },
            apiKeyProvider = { "test-api-key" }
        )

        val result = repository.askMathAssistant("مشتق تابع x^2 کدام است؟")

        assertTrue("در حالت خاموش بودن، نتیجه باید Disabled باشد", result is AiAssistantResult.Disabled)
        assertEquals("در حالت خاموش، دقیقاً صفر درخواست باید به شبکه ارسال شود", 0, mockApi.callCount)
    }

    // ۲. بررسی صریح موفقیت درخواست با مدل اصلی و کلید در هدر
    @Test
    fun `verify network request made and success returned when AI switch is ON`() = runBlocking {
        val settingsDao = database.settingsDao()
        settingsDao.saveSettings(AppSettingsEntity(id = 1, isAiEnabled = true))

        val mockApi = MockGeminiApiService(
            shouldSucceed = true,
            responseText = "مشتق تابع برابر با 2x است."
        )
        val repository = GeminiAssistantRepository(
            context = context,
            settingsDao = settingsDao,
            apiService = mockApi,
            networkChecker = { true }, // شبکه مهیا است
            apiKeyProvider = { "valid-test-key-789" }
        )

        val result = repository.askMathAssistant("مشتق x^2", isComplexMath = true)

        assertTrue("نتیجه درخواست باید حتماً Success باشد", result is AiAssistantResult.Success)
        val success = result as AiAssistantResult.Success
        assertEquals("مشتق تابع برابر با 2x است.", success.reply)
        assertEquals(GeminiAssistantRepository.MODEL_REASONING, success.modelUsed)
        assertEquals("سرویس باید دقیقاً یک بار فراخوانی شده باشد", 1, mockApi.callCount)
        assertEquals("کلید احراز هویت باید به‌درستی به متد ارسال شده باشد", "valid-test-key-789", mockApi.lastApiKeyReceived)
    }

    // ۳. بررسی کنترل‌شده مدیریت Timeout با فال‌بک روی هر ۳ مدل
    @Test
    fun `verify controlled handling of SocketTimeoutException`() = runBlocking {
        val settingsDao = database.settingsDao()
        settingsDao.saveSettings(AppSettingsEntity(id = 1, isAiEnabled = true))

        val mockApi = MockGeminiApiService(shouldTimeout = true)
        val repository = GeminiAssistantRepository(
            context = context,
            settingsDao = settingsDao,
            apiService = mockApi,
            networkChecker = { true },
            apiKeyProvider = { "test-api-key" }
        )

        val result = repository.askMathAssistant("حل معادله دیفرانسیل")

        assertTrue("در هنگام رخ دادن Timeout نتیجه باید Error باشد", result is AiAssistantResult.Error)
        val error = result as AiAssistantResult.Error
        assertEquals("Timeout", error.technicalDetail)
        assertTrue(error.userMessage.contains("Timeout") || error.userMessage.contains("طولانی") || error.userMessage.contains("دیر"))
        assertEquals("سرویس شبکه باید برای هر ۳ مدل تلاش کرده باشد", 3, mockApi.callCount)
    }

    // ۴. بررسی پرتاب مجدد CancellationException جهت حفظ ساختار لغو کوروتین
    @Test
    fun `verify CancellationException is rethrown and not swallowed`() = runBlocking {
        val settingsDao = database.settingsDao()
        settingsDao.saveSettings(AppSettingsEntity(id = 1, isAiEnabled = true))

        val mockApi = MockGeminiApiService(shouldThrowCancellation = true)
        val repository = GeminiAssistantRepository(
            context = context,
            settingsDao = settingsDao,
            apiService = mockApi,
            networkChecker = { true },
            apiKeyProvider = { "test-api-key" }
        )

        try {
            repository.askMathAssistant("تست لغو درخواست")
            fail("استثنای CancellationException باید مجدداً پرتاب شود و نادیده گرفته نشود")
        } catch (e: CancellationException) {
            assertTrue(e.message?.contains("Simulated job cancellation") == true)
        }
    }

    // ۵. بررسی تفکیک خطاهای HTTP (مانند 429 سقف درخواست)
    @Test
    fun `verify HTTP 429 rate limit is handled gracefully`() = runBlocking {
        val settingsDao = database.settingsDao()
        settingsDao.saveSettings(AppSettingsEntity(id = 1, isAiEnabled = true))

        val mockApi = MockGeminiApiService(shouldSucceed = false, errorCode = 429)
        val repository = GeminiAssistantRepository(
            context = context,
            settingsDao = settingsDao,
            apiService = mockApi,
            networkChecker = { true },
            apiKeyProvider = { "test-api-key" }
        )

        val result = repository.askMathAssistant("حل تست")

        assertTrue(result is AiAssistantResult.Error)
        val error = result as AiAssistantResult.Error
        assertTrue(error.technicalDetail?.startsWith("HTTP 429") == true)
        assertTrue(error.technicalDetail?.contains("gemini-3.1-flash-lite") == true)
        assertTrue(error.userMessage.contains("سقف مجاز"))
    }

    // ۶. بررسی وضعیت عدم دسترسی به اینترنت
    @Test
    fun `verify offline state when network is unavailable`() = runBlocking {
        val settingsDao = database.settingsDao()
        settingsDao.saveSettings(AppSettingsEntity(id = 1, isAiEnabled = true))

        val mockApi = MockGeminiApiService()
        val repository = GeminiAssistantRepository(
            context = context,
            settingsDao = settingsDao,
            apiService = mockApi,
            networkChecker = { false },
            apiKeyProvider = { "test-api-key" }
        )

        val result = repository.askMathAssistant("حل تست")

        assertTrue(result is AiAssistantResult.Offline)
        assertEquals("وقتی اینترنت قطع است هیچ درخواست شبکه‌ای نباید ارسال شود", 0, mockApi.callCount)
    }

    // =========================================================================
    // تست‌های اختصاصی نسخه ۱.۰.۳ (Fallback، حذف thinkingConfig و مدیریت Timeout)
    // =========================================================================

    // تست جدید ۱: مدل اول تایماوت بدهد، دومی موفق شود → نتیجه Success با نام مدل دوم
    @Test
    fun `new test 1 - model 1 timeouts and model 2 succeeds returns success with model 2`() = runBlocking {
        val settingsDao = database.settingsDao()
        settingsDao.saveSettings(AppSettingsEntity(id = 1, isAiEnabled = true))

        val mockApi = MockGeminiApiService(
            customHandler = { model, callCount, _ ->
                if (callCount == 1) {
                    throw SocketTimeoutException("First model timeout")
                } else {
                    Response.success(
                        GeminiResponse(
                            candidates = listOf(
                                GeminiCandidate(
                                    content = GeminiContent(
                                        parts = listOf(GeminiPart("پاسخ مدل دوم"))
                                    )
                                )
                            )
                        )
                    )
                }
            }
        )
        val repo = GeminiAssistantRepository(context, settingsDao, mockApi, { true }, { "valid-key" })
        var reportedStatus = ""
        val result = repo.askMathAssistant("مسئله هندسه", onStatusUpdate = { reportedStatus = it })

        assertTrue(result is AiAssistantResult.Success)
        val success = result as AiAssistantResult.Success
        assertEquals("gemini-3.5-flash-lite", success.modelUsed)
        assertEquals("پاسخ مدل دوم", success.reply)
        assertEquals(2, mockApi.callCount)
        assertEquals(listOf("gemini-3.5-flash", "gemini-3.5-flash-lite"), mockApi.modelsCalled)
        assertEquals("مدل اول دیر جواب داد، مدل سریعتر را امتحان میکنم...", reportedStatus)
    }

    // تست جدید ۲: ۴۰۴ برای مدل اول و دوم، موفق برای سوم → Success با مدل سوم
    @Test
    fun `new test 2 - 404 on model 1 and 2 and model 3 succeeds returns success with model 3`() = runBlocking {
        val settingsDao = database.settingsDao()
        settingsDao.saveSettings(AppSettingsEntity(id = 1, isAiEnabled = true))

        val mockApi = MockGeminiApiService(
            customHandler = { model, callCount, _ ->
                if (callCount < 3) {
                    Response.error(
                        404,
                        "{\"error\":\"not found\"}".toResponseBody("application/json".toMediaTypeOrNull())
                    )
                } else {
                    Response.success(
                        GeminiResponse(
                            candidates = listOf(
                                GeminiCandidate(
                                    content = GeminiContent(
                                        parts = listOf(GeminiPart("پاسخ مدل سوم"))
                                    )
                                )
                            )
                        )
                    )
                }
            }
        )
        val repo = GeminiAssistantRepository(context, settingsDao, mockApi, { true }, { "valid-key" })
        val result = repo.askMathAssistant("مسئله حسابان")

        assertTrue(result is AiAssistantResult.Success)
        val success = result as AiAssistantResult.Success
        assertEquals("gemini-3.1-flash-lite", success.modelUsed)
        assertEquals("پاسخ مدل سوم", success.reply)
        assertEquals(3, mockApi.callCount)
        assertEquals(listOf("gemini-3.5-flash", "gemini-3.5-flash-lite", "gemini-3.1-flash-lite"), mockApi.modelsCalled)
    }

    // تست جدید ۳: ۴۰۰ → هیچ fallback انجام نشود
    @Test
    fun `new test 3 - 400 error halts immediately without any fallback`() = runBlocking {
        val settingsDao = database.settingsDao()
        settingsDao.saveSettings(AppSettingsEntity(id = 1, isAiEnabled = true))

        val mockApi = MockGeminiApiService(
            shouldSucceed = false,
            errorCode = 400
        )
        val repo = GeminiAssistantRepository(context, settingsDao, mockApi, { true }, { "valid-key" })
        val result = repo.askMathAssistant("صورت مسئله اشتباه")

        assertTrue(result is AiAssistantResult.Error)
        val error = result as AiAssistantResult.Error
        assertEquals("HTTP 400 - gemini-3.5-flash", error.technicalDetail)
        assertEquals("روی خطای ۴۰۰ نباید هیچ fallback انجام شود", 1, mockApi.callCount)
        assertEquals("gemini-3.5-flash", mockApi.lastModelCalled)
    }

    // تست جدید ۴: هر سه مدل تایماوت → Error با پیام مناسب
    @Test
    fun `new test 4 - all three models timeout returns error with appropriate message`() = runBlocking {
        val settingsDao = database.settingsDao()
        settingsDao.saveSettings(AppSettingsEntity(id = 1, isAiEnabled = true))

        val mockApi = MockGeminiApiService(shouldTimeout = true)
        val repo = GeminiAssistantRepository(context, settingsDao, mockApi, { true }, { "valid-key" })
        val result = repo.askMathAssistant("تایماوت هر ۳ مدل")

        assertTrue(result is AiAssistantResult.Error)
        val error = result as AiAssistantResult.Error
        assertEquals("Timeout", error.technicalDetail)
        assertTrue("پیام باید حاوی دیر جواب دادن باشد", error.userMessage.contains("دیر") || error.userMessage.contains("Timeout"))
        assertEquals("هر سه مدل باید در تایماوت امتحان شده باشند", 3, mockApi.callCount)
        assertEquals(listOf("gemini-3.5-flash", "gemini-3.5-flash-lite", "gemini-3.1-flash-lite"), mockApi.modelsCalled)
    }

    // تست جدید ۵: بررسی اینکه درخواست ارسالی thinkingConfig ندارد
    @Test
    fun `new test 5 - verify outgoing request has no thinkingConfig in both fast and reasoning modes`() = runBlocking {
        val settingsDao = database.settingsDao()
        settingsDao.saveSettings(AppSettingsEntity(id = 1, isAiEnabled = true))

        val mockApi = MockGeminiApiService(shouldSucceed = true)
        val repo = GeminiAssistantRepository(context, settingsDao, mockApi, { true }, { "valid-key" })

        // حالت سریع
        repo.askMathAssistant("سوال سریع", isComplexMath = false)
        val fastConfig = mockApi.lastRequestReceived?.generationConfig
        org.junit.Assert.assertNull("thinkingConfig در حالت سریع باید null باشد", fastConfig?.thinkingConfig)
        assertEquals(0.7f, fastConfig?.temperature ?: 0f, 0.01f)

        // حالت استدلال
        repo.askMathAssistant("سوال دقیق", isComplexMath = true)
        val reasoningConfig = mockApi.lastRequestReceived?.generationConfig
        org.junit.Assert.assertNull("thinkingConfig در حالت استدلال عمیق نیز باید کاملاً null باشد", reasoningConfig?.thinkingConfig)
        assertEquals(0.4f, reasoningConfig?.temperature ?: 0f, 0.01f)
    }
}
