package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.UserBadgeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BadgeDao {
    @Query("SELECT * FROM user_badges ORDER BY isUnlocked DESC, points DESC")
    fun getAllBadges(): Flow<List<UserBadgeEntity>>

    @Query("SELECT * FROM user_badges WHERE isUnlocked = 1 ORDER BY unlockedAt DESC")
    fun getUnlockedBadges(): Flow<List<UserBadgeEntity>>

    @Query("SELECT * FROM user_badges WHERE badgeKey = :badgeKey LIMIT 1")
    suspend fun getBadgeByKey(badgeKey: String): UserBadgeEntity?

    /**
     * باز کردن مدال به صورت کاملاً ایمن و غیرتکراری:
     * فقط ردیف‌هایی را آپدیت می‌کند که قبلاً باز نشده باشند (isUnlocked = 0).
     * مقدار بازگشتی: تعداد ردیف‌های تغییر یافته (اگر ۱ باشد، یعنی مدال تازه باز شده و باید امتیاز اضافه شود؛ اگر ۰ باشد یعنی قبلاً باز بوده و هیچ امتیاز تکراری داده نمی‌شود).
     */
    @Query("UPDATE user_badges SET isUnlocked = 1, unlockedAt = :timestamp WHERE badgeKey = :badgeKey AND isUnlocked = 0")
    suspend fun safeUnlockBadge(badgeKey: String, timestamp: Long = System.currentTimeMillis()): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertInitialBadges(badges: List<UserBadgeEntity>)

    @Query("SELECT SUM(points) FROM user_badges WHERE isUnlocked = 1")
    fun getTotalUnlockedPoints(): Flow<Int?>
}
