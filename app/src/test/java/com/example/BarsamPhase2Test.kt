package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.audio.SoundManager
import com.example.data.bank.BarsamQuestionBank
import com.example.data.local.BarsamDatabase
import com.example.data.local.entity.AppSettingsEntity
import com.example.data.local.entity.MistakeNoteEntity
import com.example.data.local.entity.QuizResultEntity
import com.example.data.local.entity.UserBadgeEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class BarsamPhase2Test {

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

    // ==========================================
    // ۱. اعتبارسنجی بانک ۶۰ سوال ریاضی
    // ==========================================

    @Test
    fun `verify exactly 60 questions exist in bank`() {
        val questions = BarsamQuestionBank.questions
        assertEquals("تعداد کل سوالات باید دقیقا ۶۰ عدد باشد", 60, questions.size)
    }

    @Test
    fun `verify exact distribution of 20 questions per grade and 5 per difficulty`() {
        val questions = BarsamQuestionBank.questions
        val grades = listOf(10, 11, 12)
        val difficulties = listOf("آسان", "متوسط", "سخت", "کنکوری")

        for (grade in grades) {
            val gradeQuestions = questions.filter { it.grade == grade }
            assertEquals("پایه $grade باید دقیقا ۲۰ سوال داشته باشد", 20, gradeQuestions.size)

            for (difficulty in difficulties) {
                val diffCount = gradeQuestions.count { it.difficulty == difficulty }
                assertEquals(
                    "پایه $grade و سطح $difficulty باید دقیقا ۵ سوال داشته باشد",
                    5,
                    diffCount
                )
            }
        }
    }

    @Test
    fun `verify all questions have unique IDs and valid options`() {
        val questions = BarsamQuestionBank.questions
        val ids = questions.map { it.id }
        assertEquals("تمام شناسه‌های سوالات باید یکتا باشند", ids.size, ids.toSet().size)

        for (q in questions) {
            assertTrue("متن سوال نباید خالی باشد: ${q.id}", q.questionText.isNotBlank())
            assertTrue("گزینه ۱ نباید خالی باشد: ${q.id}", q.option1.isNotBlank())
            assertTrue("گزینه ۲ نباید خالی باشد: ${q.id}", q.option2.isNotBlank())
            assertTrue("گزینه ۳ نباید خالی باشد: ${q.id}", q.option3.isNotBlank())
            assertTrue("گزینه ۴ نباید خالی باشد: ${q.id}", q.option4.isNotBlank())
            assertTrue("اندیس گزینه صحیح باید بین ۰ تا ۳ باشد: ${q.id}", q.correctOptionIndex in 0..3)
            assertTrue("پاسخ تشریحی نباید خالی باشد: ${q.id}", q.explanation.isNotBlank())
            assertTrue("سوال باید تایید شده باشد: ${q.id}", q.isVerified)
        }
    }

    // ==========================================
    // ۲. اعتبارسنجی پایگاه داده Room و قیدهای یکتایی
    // ==========================================

    @Test
    fun `verify questions can be inserted and queried from Room`() = runBlocking {
        val questionDao = database.questionDao()
        questionDao.insertQuestions(BarsamQuestionBank.questions)

        val count = questionDao.getQuestionsCount()
        assertEquals(60, count)

        val g10Questions = questionDao.getQuizQuestions(10, null, "کنکوری", 5)
        assertEquals(5, g10Questions.size)
        assertTrue(g10Questions.all { it.grade == 10 && it.difficulty == "کنکوری" })
    }

    @Test
    fun `verify safe badge unlock prevents duplicate awards`() = runBlocking {
        val badgeDao = database.badgeDao()
        val testBadge = UserBadgeEntity(
            badgeKey = "TEST_FIRST_QUIZ",
            title = "اولین آزمون",
            description = "تکمیل اولین آزمون",
            points = 50,
            iconName = "military_tech",
            isUnlocked = false
        )
        badgeDao.insertInitialBadges(listOf(testBadge))

        // باز کردن برای اولین بار باید ۱ ردیف تغییر کند (موفق)
        val firstUnlockRows = badgeDao.safeUnlockBadge("TEST_FIRST_QUIZ")
        assertEquals("باز شدن اولیه مدال باید ۱ ردیف را آپدیت کند", 1, firstUnlockRows)

        // باز کردن مجدد (مثلا با باز و بسته کردن صفحه) نباید هیچ ردیفی را تغییر دهد (جلوگیری از مدال تکراری)
        val secondUnlockRows = badgeDao.safeUnlockBadge("TEST_FIRST_QUIZ")
        assertEquals("تلاش برای باز کردن مجدد مدال نباید موفق شود (۰ ردیف)", 0, secondUnlockRows)

        val badge = badgeDao.getBadgeByKey("TEST_FIRST_QUIZ")
        assertNotNull(badge)
        assertTrue(badge!!.isUnlocked)
    }

    @Test
    fun `verify quiz result insertion and mistake notes`() = runBlocking {
        val quizDao = database.quizDao()
        val mistakeDao = database.mistakeDao()

        val resultId = quizDao.insertQuizResult(
            QuizResultEntity(
                grade = 12,
                topic = "مشتق",
                difficulty = "کنکوری",
                totalQuestions = 10,
                correctCount = 8,
                wrongCount = 2,
                unansweredCount = 0,
                percentage = 73.3,
                durationSeconds = 600
            )
        )
        assertTrue(resultId > 0)

        val count = quizDao.getQuizCount().first()
        assertEquals(1, count)

        // درج اشتباه
        val mistakeId = mistakeDao.insertMistake(
            MistakeNoteEntity(
                questionId = "G12_KONK_01",
                grade = 12,
                topic = "مشتق",
                questionText = "سوال تست",
                correctOption = "200π",
                userChosenOption = "100π",
                explanation = "راه حل",
                mistakeReason = "بی‌دقتی محاسباتی"
            )
        )
        assertTrue(mistakeId > 0)

        val unresolvedCount = mistakeDao.getUnresolvedMistakesCount().first()
        assertEquals(1, unresolvedCount)

        mistakeDao.updateResolvedStatus(mistakeId, true)
        val resolvedCount = mistakeDao.getUnresolvedMistakesCount().first()
        assertEquals(0, resolvedCount)
    }

    @Test
    fun `verify settings persistence and switch toggles`() = runBlocking {
        val settingsDao = database.settingsDao()
        settingsDao.saveSettings(
            AppSettingsEntity(
                id = 1,
                isSoundEnabled = true,
                isAiEnabled = false,
                dailyStudyTargetMinutes = 60,
                defaultGrade = 11,
                totalScore = 150
            )
        )

        var settings = settingsDao.getSettingsDirect()
        assertNotNull(settings)
        assertTrue(settings!!.isSoundEnabled)
        assertFalse(settings.isAiEnabled)

        // تغییر تنظیمات صدا و هوش مصنوعی
        settingsDao.setSoundEnabled(false)
        settingsDao.setAiEnabled(true)

        settings = settingsDao.getSettingsDirect()
        assertFalse(settings!!.isSoundEnabled)
        assertTrue(settings.isAiEnabled)
    }

    // ==========================================
    // ۳. اعتبارسنجی ماژول صدا SoundManager
    // ==========================================

    @Test
    fun `verify sound manager respects in-app mute setting`() {
        val soundManager = SoundManager(context)
        soundManager.isSoundEnabled = false
        assertFalse("وقتی صدای برنامه خاموش است نباید صدا پخش شود", soundManager.canPlayAudio())

        soundManager.isSoundEnabled = true
        // در شبیه‌ساز یا حالت عادی، canPlayAudio مقدار منطقی بر اساس رینگر می‌دهد
        val canPlay = soundManager.canPlayAudio()
        // تست مطمئن می‌شود که متد بدون کرش کار می‌کند
        assertNotNull(canPlay)
    }
}
