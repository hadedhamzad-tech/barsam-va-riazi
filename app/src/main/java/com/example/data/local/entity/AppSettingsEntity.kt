package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * موجودیت ذخیره تنظیمات پایدار برنامه برسام و ریاضی
 */
@Entity(tableName = "app_settings")
data class AppSettingsEntity(
    @PrimaryKey
    val id: Int = 1, // ردیف ثابت تنظیمات
    val isSoundEnabled: Boolean = true, // وضعیت صدای برنامه
    val isAiEnabled: Boolean = false, // سوییچ فعال‌سازی هوش مصنوعی (پیش‌فرض خاموش)
    val dailyStudyTargetMinutes: Int = 60, // هدف روزانه مطالعه ریاضی به دقیقه
    val defaultGrade: Int = 10, // پایه تحصیلی پیش‌فرض کاربر
    val totalScore: Int = 0 // مجموع امتیازهای کسب‌شده
)
