package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * موجودیت مدال‌ها و دستاوردهای کاربر با کلید یکتا برای جلوگیری از ثبت تکراری
 */
@Entity(tableName = "user_badges")
data class UserBadgeEntity(
    @PrimaryKey
    val badgeKey: String, // کلید یکتا: قید یکتایی قطعی در سطح پایگاه داده (مثلا: "FIRST_QUIZ")
    val title: String, // عنوان فارسی مدال
    val description: String, // شرح شرط دریافت مدال
    val points: Int, // امتیاز اختصاص‌یافته به این مدال
    val iconName: String, // شناسه آیکون نمایشی
    val isUnlocked: Boolean = false, // وضعیت باز شدن
    val unlockedAt: Long = 0L // زمان دریافت (میلی‌ثانیه)
)
