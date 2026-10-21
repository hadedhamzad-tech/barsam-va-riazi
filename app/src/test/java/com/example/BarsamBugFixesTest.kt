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

    // تست ۴: آزمون خالی نباید هیچ امتیازی یا مدالی بدهد و از اجرای دوبار جلوگیری شود
    @Test
    fun `test empty quiz with zero answers awards zero score and no badges, and prevents double finish`() = runBlocking {
        val viewModel = MainViewModel(app, database)

        viewModel.startCustomQuiz(grade = 10, topic = "همه مباحث", difficulty = "آسان", count = 5, durationMinutes = 10)
        org.robolectric.shadows.ShadowLooper.runUiThreadTasks()

        // بدون ثبت هیچ پاسخی، آزمون به پایان می‌رسد
        viewModel.finishActiveQuiz()
        org.robolectric.shadows.ShadowLooper.runUiThreadTasks()

        // فراخوانی مجدد بلافاصله برای تست جلوگیری از ذخیره دوبل
        viewModel.finishActiveQuiz()
        org.robolectric.shadows.ShadowLooper.runUiThreadTasks()

        val settings = database.settingsDao().getSettingsDirect()
        assertEquals("برای آزمون کاملاً سفید، هیچ امتیازی نباید تعلق گیرد", 0, settings?.totalScore ?: 0)

        val firstQuizBadge = database.badgeDao().getBadgeByKey("FIRST_QUIZ")
        assertFalse("برای آزمون سفید، مدال اولین آزمون نباید باز شود", firstQuizBadge?.isUnlocked ?: false)

        val quizCount = database.quizDao().getQuizCount().first()
        assertEquals("تنها یک رکورد کارنامه باید ثبت شده باشد و فراخوانی دوم مسدود شده باشد", 1, quizCount)
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
        // ۷۳.۳۳۳۳۳۳۳ -> ۷۳٫۳٪
        assertEquals("۷۳٫۳٪", PersianUtils.formatPercentage(73.333333))

        // ۱۰۰.۰ -> ۱۰۰٫۰٪
        assertEquals("۱۰۰٫۰٪", PersianUtils.formatPercentage(100.0))

        // ۰.۰ -> ۰٫۰٪
        assertEquals("۰٫۰٪", PersianUtils.formatPercentage(0.0))

        // null -> ۰٫۰٪
        assertEquals("۰٫۰٪", PersianUtils.formatPercentage(null))

        // ۶۶.۶۶۶۶ -> ۶۶٫۷٪
        assertEquals("۶۶٫۷٪", PersianUtils.formatPercentage(66.6666))
    }

    // تست ۸: شناسه‌های مدل‌های Gemini و پیام خطای ۴۰۴
    @Test
    fun `test Gemini model constants and 404 error message clarity`() = runBlocking {
        assertEquals("gemini-2.5-flash", GeminiAssistantRepository.MODEL_FAST)
        assertEquals("gemini-2.5-pro", GeminiAssistantRepository.MODEL_REASONING)

        val mockApi = object : GeminiApiService {
            override suspend fun generateContent(
                model: String,
                apiKey: String,
                request: GeminiRequest
            ): Response<GeminiResponse> {
                return Response.error(
                    404,
                    "{\"error\":\"Model not found\"}".toResponseBody("application/json".toMediaTypeOrNull())
                )
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
        assertTrue(result is AiAssistantResult.Error)
        val error = result as AiAssistantResult.Error
        assertTrue("پیام باید شامل کد ۴۰۴ یا عبارت در دسترس نیست باشد", error.userMessage.contains("۴۰۴") || error.userMessage.contains("یافت نشد"))
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

