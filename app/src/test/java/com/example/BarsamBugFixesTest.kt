package com.example

import android.app.Application
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.bank.BarsamQuestionBank
import com.example.data.local.BarsamDatabase
import com.example.data.local.entity.AppSettingsEntity
import com.example.data.local.entity.UserBadgeEntity
import com.example.data.remote.AiAssistantResult
import com.example.data.remote.GeminiApiService
import com.example.data.remote.GeminiRequest
import com.example.data.remote.GeminiResponse
import com.example.data.repository.GeminiAssistantRepository
import com.example.ui.navigation.Screen
import com.example.ui.viewmodel.MainViewModel
import com.example.util.PersianUtils
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import retrofit2.Response

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class BarsamBugFixesTest {

    private lateinit var app: Application
    private lateinit var database: BarsamDatabase

    @Before
    fun setup() = runBlocking {
        app = ApplicationProvider.getApplicationContext()
        database = Room.inMemoryDatabaseBuilder(app, BarsamDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        // درج سوالات اولیه در دیتابیس تست
        database.questionDao().insertQuestions(BarsamQuestionBank.questions)

        // درج مدال‌های اولیه
        database.badgeDao().insertInitialBadges(
            listOf(
                UserBadgeEntity(
                    badgeKey = "FIRST_QUIZ",
                    title = "اولین گام ریاضی",
                    description = "تکمیل اولین آزمون تستی در برنامه",
                    points = 50,
                    iconName = "military_tech"
                ),
                UserBadgeEntity(
                    badgeKey = "PERFECT_SCORE",
                    title = "درصد ۱۰۰ درخشان",
                    description = "کسب درصد ۱۰۰٪ در یک آزمون ریاضی",
                    points = 150,
                    iconName = "emoji_events"
                ),
                UserBadgeEntity(
                    badgeKey = "KONKUR_HERO",
                    title = "قهرمان تست‌های کنکوری",
                    description = "پاسخ موفق به سوالات سطح دشواری کنکوری",
                    points = 120,
                    iconName = "workspace_premium"
                )
            )
        )

        // درج تنظیمات اولیه با امتیاز ۰
        database.settingsDao().saveSettings(
            AppSettingsEntity(
                id = 1,
                isSoundEnabled = true,
                isAiEnabled = false,
                totalScore = 0
            )
        )
    }

    @After
    fun tearDown() {
        database.close()
    }

    // تست ۱: انصراف از آزمون باید تایمر را لغو کرده، وضعیت را پاک کند و هیچ نتیجه‌ای ثبت نکند
    @Test
    fun `test cancelActiveQuiz clears state, timer and saves no results`() = runBlocking {
        val viewModel = MainViewModel(app, database)

        // شروع یک آزمون
        viewModel.startCustomQuiz(
            grade = 10,
            topic = "همه مباحث",
            difficulty = "آسان",
            count = 5,
            durationMinutes = 10
        )
        for (i in 0..20) {
            if (viewModel.currentScreen.value is Screen.QuizActive) break
            kotlinx.coroutines.delay(20)
            org.robolectric.shadows.ShadowLooper.runUiThreadTasks()
        }

        assertEquals("آزمون باید به صفحه فعال برود", Screen.QuizActive, viewModel.currentScreen.value)
        assertEquals(5, viewModel.activeQuestions.value.size)

        // انتخاب یک گزینه توسط کاربر
        viewModel.selectQuizAnswer(0, 1)
        assertEquals(1, viewModel.userAnswers.value.size)

        // انصراف از آزمون
        viewModel.cancelActiveQuiz()
        org.robolectric.shadows.ShadowLooper.runUiThreadTasks()

        // بررسی پاکسازی کامل وضعیت
        assertEquals("صفحه باید به داشبورد بازگردد", Screen.Dashboard, viewModel.currentScreen.value)
        assertTrue("لیست سوالات فعال باید خالی شده باشد", viewModel.activeQuestions.value.isEmpty())
        assertTrue("پاسخ‌های کاربر باید پاک شده باشد", viewModel.userAnswers.value.isEmpty())
        assertEquals("زمان باقیمانده باید صفر شود", 0, viewModel.remainingSeconds.value)
        assertNull("آخرین نتیجه آزمون باید پاک شود", viewModel.latestQuizResult.value)

        // بررسی عدم ثبت در دیتابیس
        val quizCount = database.quizDao().getQuizCount().first()
        assertEquals("هیچ رکوردی در دیتابیس نباید ثبت شده باشد", 0, quizCount)

        // بررسی عدم اعطای امتیاز
        val settings = database.settingsDao().getSettingsDirect()
        assertEquals("امتیاز کاربر باید صفر باقی مانده باشد", 0, settings?.totalScore ?: 0)
    }

    // تست ۲: دکمه پاک کردن پاسخ برای جلوگیری از نمره منفی
    @Test
    fun `test clearQuizAnswer removes selected answer for current question`() = runBlocking {
        val viewModel = MainViewModel(app, database)

        viewModel.startCustomQuiz(grade = 10, topic = "همه مباحث", difficulty = "آسان", count = 5, durationMinutes = 10)
        for (i in 0..20) {
            if (viewModel.currentScreen.value is Screen.QuizActive) break
            kotlinx.coroutines.delay(20)
            org.robolectric.shadows.ShadowLooper.runUiThreadTasks()
        }

        // کاربر سوال ۰ را انتخاب می‌کند
        viewModel.selectQuizAnswer(0, 2)
        assertEquals(2, viewModel.userAnswers.value[0])

        // کاربر پاسخ را پاک می‌کند تا سوال بدون پاسخ رها شود
        viewModel.clearQuizAnswer(0)
        assertNull("پاسخ سوال ۰ باید پاک شده باشد", viewModel.userAnswers.value[0])
        assertFalse("کلید سوال ۰ نباید در مپ پاسخ‌ها باشد", viewModel.userAnswers.value.containsKey(0))
    }

    // تست ۳: امتیاز مدال از موجودیت مدال خوانده شود (منبع واحد حقیقت) و مضاعف شمرده نشود
    @Test
    fun `test badge points read from entity and added once as single source of truth`() = runBlocking {
        val badge = database.badgeDao().getBadgeByKey("FIRST_QUIZ")
        assertNotNull("مدال باید در دیتابیس موجود باشد", badge)
        assertEquals(50, badge!!.points)

        val initialScore = database.settingsDao().getSettingsDirect()?.totalScore ?: 0
        assertEquals(0, initialScore)

        // باز کردن ایمن مدال
        val updatedRows = database.badgeDao().safeUnlockBadge("FIRST_QUIZ")
        assertEquals(1, updatedRows)

        // اضافه کردن امتیاز بر اساس فیلد points موجودیت (نه عدد هاردکد)
        database.settingsDao().addScore(badge.points)

        val updatedScore = database.settingsDao().getSettingsDirect()?.totalScore ?: 0
        assertEquals("امتیاز باید دقیقاً برابر با امتیاز مدال (۵۰) باشد", 50, updatedScore)

        // تلاش مجدد برای باز کردن همان مدال باید ۰ برگرداند
        val secondAttempt = database.badgeDao().safeUnlockBadge("FIRST_QUIZ")
        assertEquals("تلاش دوم نباید ردیفی را تغییر دهد", 0, secondAttempt)

        // امتیاز نباید مجدداً افزوده شود
        val finalScore = database.settingsDao().getSettingsDirect()?.totalScore ?: 0
        assertEquals("امتیاز نباید دوبار اضافه شود", 50, finalScore)
    }

    // تست ۴: آزمون خالی با صفر پاسخ نباید در دیتابیس ثبت شود و کاربر به داشبورد هدایت می‌شود
    @Test
    fun `test empty quiz with zero answers does not save to database and returns to dashboard`() = runBlocking {
        val viewModel = MainViewModel(app, database)

        viewModel.startCustomQuiz(grade = 10, topic = "همه مباحث", difficulty = "آسان", count = 5, durationMinutes = 10)
        org.robolectric.shadows.ShadowLooper.runUiThreadTasks()

        // بدون ثبت هیچ پاسخی، آزمون به پایان می‌رسد
        viewModel.finishActiveQuiz()
        org.robolectric.shadows.ShadowLooper.runUiThreadTasks()

        val settings = database.settingsDao().getSettingsDirect()
        assertEquals("برای آزمون کاملاً سفید، هیچ امتیازی نباید تعلق گیرد", 0, settings?.totalScore ?: 0)

        val firstQuizBadge = database.badgeDao().getBadgeByKey("FIRST_QUIZ")
        assertFalse("برای آزمون سفید، مدال اولین آزمون نباید باز شود", firstQuizBadge?.isUnlocked ?: false)

        val quizCount = database.quizDao().getQuizCount().first()
        assertEquals("برای آزمون کاملاً سفید نباید رکوردی در دیتابیس ثبت شود تا میانگین خراب نشود", 0, quizCount)
        assertEquals("کاربر باید به صفحه اصلی هدایت شده باشد", Screen.Dashboard, viewModel.currentScreen.value)
    }

    // تست ۵: انتخاب مبحث و اعتبارسنجی تعداد سوالات بدون فیلتر شل یا fallback پنهان
    @Test
    fun `test topic selection and question count validation without silent fallback`() = runBlocking {
        val viewModel = MainViewModel(app, database)

        val topicsGrade10 = viewModel.getTopicsForGrade(10)
        assertTrue("پایه ۱۰ باید دارای مباحث باشد", topicsGrade10.isNotEmpty())

        val firstTopic = topicsGrade10.first()
        val count = viewModel.getAvailableQuizQuestionCount(10, firstTopic, "آسان")
        assertTrue("تعداد سوال باید بزرگتر مساوی ۰ باشد", count >= 0)

        // جستجو برای مبحث ناموجود نباید خطا بدهد و تعداد باید ۰ باشد
        val zeroCount = viewModel.getAvailableQuizQuestionCount(10, "مبحث ناموجود تستی", "آسان")
        assertEquals(0, zeroCount)

        // درخواست سوالات با فیلتر ناموجود نباید به همه سوال‌های پایه fallback کند
        val emptyQuestions = database.questionDao().getQuizQuestions(10, "مبحث ناموجود تستی", "آسان", 5)
        assertTrue("در صورت عدم وجود سوال منطبق، لیست باید خالی باشد (نه سوالات نامربوط)", emptyQuestions.isEmpty())
    }

    // تست ۶: فرمت اعداد و درصدها با ممیز فارسی و علامت ٪ در انتها
    @Test
    fun `test formatPercentage rounds to 1 decimal place with Persian comma and percent sign at end`() {
        assertEquals("۷۳٫۳٪", PersianUtils.formatPercentage(73.333333))
        assertEquals("۱۰۰٫۰٪", PersianUtils.formatPercentage(100.0))
        assertEquals("۰٫۰٪", PersianUtils.formatPercentage(0.0))
        assertEquals("۰٫۰٪", PersianUtils.formatPercentage(null))
        assertEquals("۶۶٫۷٪", PersianUtils.formatPercentage(66.6666))
    }

    // تست ۷: تفکیک دقیق متن‌های ریاضی و ایزولاسیون با LRI/PDI (مشکل ۱)
    @Test
    fun `test formatMathText isolates math expressions with Unicode LRI and PDI`() {
        val lri = PersianUtils.LRI
        val pdi = PersianUtils.PDI

        // ۱. بازه‌های عددی
        assertEquals("${lri}(-2, 2)${pdi}", PersianUtils.formatMathText("(-2, 2)"))
        assertEquals("${lri}[-2, 2]${pdi}", PersianUtils.formatMathText("[-2, 2]"))
        assertEquals("${lri}(-∞, 2)${pdi}", PersianUtils.formatMathText("(-∞, 2)"))

        // ۲. رادیکال و ضرایب
        assertEquals("${lri}2√5${pdi}", PersianUtils.formatMathText("2√5"))

        // ۳. مثلثات و توان
        assertEquals("${lri}1 - 2sin²(x)${pdi}", PersianUtils.formatMathText("1 - 2sin²(x)"))

        // ۴. حد و کسر جبری
        assertEquals("${lri}lim(x→3) (x² - 9)/(x - 3)${pdi}", PersianUtils.formatMathText("lim(x→3) (x² - 9)/(x - 3)"))

        // ۵. لگاریتم و زیرنویس
        assertEquals("${lri}log₂(4 - x²)${pdi}", PersianUtils.formatMathText("log₂(4 - x²)"))

        // ۶. کسرهای شامل پی
        assertEquals("${lri}3π/2${pdi}", PersianUtils.formatMathText("3π/2"))

        // ۷. نقاط مختصاتی ترکیب با واو فارسی
        assertEquals("${lri}A(1, 2)${pdi} و ${lri}B(4, 6)${pdi}", PersianUtils.formatMathText("A(1, 2) و B(4, 6)"))

        // ۸. متن مختلط فارسی و فرمول ریاضی
        val mixedText = "فاصله نقطه A(1, 2) تا خط برابر √5 است."
        val expectedMixed = "فاصله نقطه ${lri}A(1, 2)${pdi} تا خط برابر ${lri}√5${pdi} است."
        assertEquals(expectedMixed, PersianUtils.formatMathText(mixedText))

        // ۹. ایدمپوتنت بودن (عدم بسته‌بندی مجدد در صورت فراخوانی دوباره)
        val once = PersianUtils.formatMathText("(-2, 2)")
        val twice = PersianUtils.formatMathText(once)
        assertEquals(once, twice)
    }

    // تست ۸: شناسه‌های مدل‌های Gemini و مکانیزم پشتیبان ترتیبی Fallback (نسخه ۱.۰.۳)
    @Test
    fun `test Gemini model constants and fallback retry on 404`() = runBlocking {
        assertEquals("gemini-3.5-flash", GeminiAssistantRepository.MODEL_FAST)
        assertEquals("gemini-3.5-flash", GeminiAssistantRepository.MODEL_REASONING)
        assertEquals(
            listOf("gemini-3.5-flash", "gemini-3.5-flash-lite", "gemini-3.1-flash-lite"),
            GeminiAssistantRepository.FALLBACK_MODELS
        )

        var generateCallCount = 0
        var modelUsedInGenerate = ""

        val mockApi = object : GeminiApiService {
            override suspend fun generateContent(
                model: String,
                apiKey: String,
                request: GeminiRequest
            ): Response<GeminiResponse> {
                generateCallCount++
                modelUsedInGenerate = model
                return if (model == "gemini-3.5-flash") {
                    // شبیه‌سازی خطای ۴۰۴ برای مدل اول
                    Response.error(
                        404,
                        "{\"error\":\"models/gemini-3.5-flash not found\"}".toResponseBody("application/json".toMediaTypeOrNull())
                    )
                } else if (model == "gemini-3.5-flash-lite") {
                    // پاسخ موفق برای مدل دوم fallback
                    Response.success(
                        GeminiResponse(
                            candidates = listOf(
                                com.example.data.remote.GeminiCandidate(
                                    content = com.example.data.remote.GeminiContent(
                                        parts = listOf(com.example.data.remote.GeminiPart("پاسخ مدل جایگزین"))
                                    )
                                )
                            )
                        )
                    )
                } else {
                    Response.error(400, "{}".toResponseBody("application/json".toMediaTypeOrNull()))
                }
            }

            override suspend fun listModels(apiKey: String): Response<com.example.data.remote.GeminiModelListResponse> {
                return Response.success(com.example.data.remote.GeminiModelListResponse(emptyList()))
            }
        }

        val settingsDao = database.settingsDao()
        settingsDao.saveSettings(AppSettingsEntity(id = 1, isAiEnabled = true))

        val repo = GeminiAssistantRepository(
            context = app,
            settingsDao = settingsDao,
            apiService = mockApi,
            networkChecker = { true },
            apiKeyProvider = { "dummy-key" }
        )

        val result = repo.askMathAssistant("سوال تستی")
        assertEquals("درخواست باید دوبار ارسال شده باشد (بار اول مدل پیش‌فرض، بار دوم مدل جایگزین دوم)", 2, generateCallCount)
        assertEquals("مدل استفاده‌شده در درخواست دوم باید gemini-3.5-flash-lite باشد", "gemini-3.5-flash-lite", modelUsedInGenerate)
        assertTrue("نتیجه نهایی پس از پشتیبان باید موفقیت‌آمیز باشد", result is AiAssistantResult.Success)
        val success = result as AiAssistantResult.Success
        assertEquals("نام مدل جایگزین باید در خروجی ثبت شده باشد", "gemini-3.5-flash-lite", success.modelUsed)
    }

    @Test
    fun `test MainViewModel can be instantiated via reflection by AndroidViewModelFactory`() {
        val constructor = MainViewModel::class.java.getConstructor(Application::class.java)
        assertNotNull("Constructor MainViewModel(Application) must exist for ViewModelProvider", constructor)
        val viewModel = constructor.newInstance(app)
        assertNotNull(viewModel)
        assertEquals(Screen.Dashboard, viewModel.currentScreen.value)
    }
}

