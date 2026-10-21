package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * موجودیت سوال ریاضی در پایگاه داده محلی Room
 */
@Entity(tableName = "math_questions")
data class MathQuestionEntity(
    @PrimaryKey
    val id: String, // شناسه پایدار و یکتا، مثلا G10_EASY_01
    val grade: Int, // پایه تحصیلی: 10، 11 یا 12
    val difficulty: String, // سطح دشواری: "آسان"، "متوسط"، "سخت"، "کنکوری"
    val topic: String, // مبحث درسی
    val questionText: String, // متن سوال
    val option1: String, // گزینه ۱
    val option2: String, // گزینه ۲
    val option3: String, // گزینه ۳
    val option4: String, // گزینه ۴
    val correctOptionIndex: Int, // اندیس گزینه صحیح: 0, 1, 2, 3
    val explanation: String, // راه حل تشریحی کامل آفلاین
    val isVerified: Boolean = true // تایید علمی و ریاضی
)
