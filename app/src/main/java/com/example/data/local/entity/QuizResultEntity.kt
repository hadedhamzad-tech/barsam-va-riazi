package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * موجودیت نتایج و کارنامه‌های آزمون
 */
@Entity(tableName = "quiz_results")
data class QuizResultEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val grade: Int,
    val topic: String,
    val difficulty: String,
    val totalQuestions: Int,
    val correctCount: Int,
    val wrongCount: Int,
    val unansweredCount: Int,
    val percentage: Double, // درصد محاسبه‌شده با احتساب نمره منفی
    val durationSeconds: Int // مدت زمان آزمون به ثانیه
)
