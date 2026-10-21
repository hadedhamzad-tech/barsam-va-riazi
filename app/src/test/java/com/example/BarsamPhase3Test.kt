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
        var responseText: String = "راه حل مرحله به مرحله: x = 2"
    ) : GeminiApiService {
        var callCount: Int = 0
        var lastModelCalled: String? = null
        var lastApiKeyReceived: String? = null
        var lastRequestReceived: GeminiRequest? = null

        override suspend fun generateContent(
            model: String,
            apiKey: String,
            request: GeminiRequest
        ): Response<GeminiResponse> {
            callCount++
            lastModelCalled = model
            lastApiKeyReceived = apiKey
            lastRequestReceived = request

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

    // ۲. بررسی صریح موفقیت درخواست با مدل gemini-4-argon و کلید در هدر
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

        // اطمینان قطعی از اینکه نتیجه حتماً Success است (نه Offline یا Error)
        assertTrue("نتیجه درخواست باید حتماً Success باشد", result is AiAssistantResult.Success)
        val success = result as AiAssistantResult.Success
        assertEquals("مشتق تابع برابر با 2x است.", success.reply)
        assertEquals("gemini-4-argon", success.modelUsed)
        assertEquals("سرویس باید دقیقاً یک بار فراخوانی شده باشد", 1, mockApi.callCount)
        assertEquals("کلید احراز هویت باید به‌درستی به متد ارسال شده باشد", "valid-test-key-789", mockApi.lastApiKeyReceived)
    }

    // ۳. بررسی کنترل‌شده مدیریت Timeout بدون وابستگی به اینترنت واقعی
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
        assertTrue(error.userMessage.contains("Timeout") || error.userMessage.contains("طولانی"))
        assertEquals("سرویس شبکه باید دقیقاً یک بار فراخوانی شده باشد", 1, mockApi.callCount)
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
            // انتظار می‌رود که استثنا بدون تبدیل به Error به کالبک بازگردد
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
        assertEquals("HTTP 429", error.technicalDetail)
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
            networkChecker = { false }, // شبیه‌سازی قطع اینترنت
            apiKeyProvider = { "test-api-key" }
        )

        val result = repository.askMathAssistant("حل تست")

        assertTrue(result is AiAssistantResult.Offline)
        assertEquals("وقتی اینترنت قطع است هیچ درخواست شبکه‌ای نباید ارسال شود", 0, mockApi.callCount)
    }
}
