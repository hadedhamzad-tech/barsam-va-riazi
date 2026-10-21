package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * موجودیت دفترچه اشتباهات جهت تحلیل و مرور تست‌های غلط
 */
@Entity(tableName = "mistake_notes")
data class MistakeNoteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val questionId: String,
    val grade: Int,
    val topic: String,
    val questionText: String,
    val correctOption: String,
    val userChosenOption: String,
    val explanation: String,
    val mistakeReason: String, // مثلا: "بی‌دقتی محاسباتی"، "عدم تسلط مفهومی"، "دام تستی"
    val userNote: String = "", // یادداشت اختصاصی دانش‌آموز
    val timestamp: Long = System.currentTimeMillis(),
    val isResolved: Boolean = false // آیا اشکال برطرف شده است
)
