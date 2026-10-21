package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * موجودیت تسک‌های برنامه‌ریزی مطالعه ریاضی
 */
@Entity(tableName = "study_tasks")
data class StudyTaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String, // عنوان تسک (مثلا: مرور فرمول‌های مثلثات دهم)
    val topic: String, // مبحث درسی
    val grade: Int, // پایه تحصیلی
    val targetMinutes: Int, // هدف مطالعه به دقیقه
    val completedMinutes: Int = 0, // دقایق مطالعه شده
    val isCompleted: Boolean = false, // وضعیت تکمیل
    val dateString: String, // تاریخ هدف
    val notes: String = "" // یادداشت‌ها
)
