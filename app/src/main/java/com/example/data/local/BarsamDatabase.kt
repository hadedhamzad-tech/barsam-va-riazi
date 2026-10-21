package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.bank.BarsamQuestionBank
import com.example.data.local.dao.BadgeDao
import com.example.data.local.dao.MistakeDao
import com.example.data.local.dao.QuestionDao
import com.example.data.local.dao.QuizDao
import com.example.data.local.dao.SettingsDao
import com.example.data.local.dao.StudyTaskDao
import com.example.data.local.entity.AppSettingsEntity
import com.example.data.local.entity.MathQuestionEntity
import com.example.data.local.entity.MistakeNoteEntity
import com.example.data.local.entity.QuizResultEntity
import com.example.data.local.entity.StudyTaskEntity
import com.example.data.local.entity.UserBadgeEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * پایگاه داده محلی پایدار Room برای برنامه برسام و ریاضی
 * نسخه: 1 (بدون استفاده از fallbackToDestructiveMigration برای حفظ اطلاعات کاربر)
 */
@Database(
    entities = [
        MathQuestionEntity::class,
        QuizResultEntity::class,
        MistakeNoteEntity::class,
        StudyTaskEntity::class,
        UserBadgeEntity::class,
        AppSettingsEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class BarsamDatabase : RoomDatabase() {

    abstract fun questionDao(): QuestionDao
    abstract fun quizDao(): QuizDao
    abstract fun mistakeDao(): MistakeDao
    abstract fun studyTaskDao(): StudyTaskDao
    abstract fun badgeDao(): BadgeDao
    abstract fun settingsDao(): SettingsDao

    companion object {
        @Volatile
        private var INSTANCE: BarsamDatabase? = null

        private val INITIAL_BADGES = listOf(
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
                badgeKey = "QUIZ_STREAK_5",
                title = "استمرار در آزمون",
                description = "ثبت حداقل ۵ کارنامه در برنامه",
                points = 100,
                iconName = "local_fire_department"
            ),
            UserBadgeEntity(
                badgeKey = "RESOLVE_MISTAKES",
                title = "استاد رفع اشکال",
                description = "بررسی و حل اشتباهات در دفترچه اشتباهات",
                points = 80,
                iconName = "auto_fix_high"
            ),
            UserBadgeEntity(
                badgeKey = "KONKUR_HERO",
                title = "قهرمان تست‌های کنکوری",
                description = "پاسخ موفق به سوالات سطح دشواری کنکوری",
                points = 120,
                iconName = "workspace_premium"
            ),
            UserBadgeEntity(
                badgeKey = "PERSISTENT_STUDENT",
                title = "تلاشگر برتر",
                description = "کسب امتیاز کلی بالای ۳۰۰ در برسام و ریاضی",
                points = 100,
                iconName = "school"
            )
        )

        fun getInstance(context: Context): BarsamDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BarsamDatabase::class.java,
                    "barsam_math_database.db"
                ).addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // بارگذاری اولیه ۶۰ سوال، مدال‌ها و تنظیمات پیش‌فرض
                        CoroutineScope(Dispatchers.IO).launch {
                            val database = getInstance(context)
                            database.questionDao().insertQuestions(BarsamQuestionBank.questions)
                            database.badgeDao().insertInitialBadges(INITIAL_BADGES)
                            database.settingsDao().saveSettings(
                                AppSettingsEntity(
                                    id = 1,
                                    isSoundEnabled = true,
                                    isAiEnabled = false,
                                    dailyStudyTargetMinutes = 60,
                                    defaultGrade = 10,
                                    totalScore = 0
                                )
                            )
                        }
                    }
                }).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
