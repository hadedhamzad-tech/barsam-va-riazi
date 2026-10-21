package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.AppSettingsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SettingsDao {
    @Query("SELECT * FROM app_settings WHERE id = 1 LIMIT 1")
    fun getSettings(): Flow<AppSettingsEntity?>

    @Query("SELECT * FROM app_settings WHERE id = 1 LIMIT 1")
    suspend fun getSettingsDirect(): AppSettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSettings(settings: AppSettingsEntity)

    @Query("UPDATE app_settings SET isSoundEnabled = :enabled WHERE id = 1")
    suspend fun setSoundEnabled(enabled: Boolean)

    @Query("UPDATE app_settings SET isAiEnabled = :enabled WHERE id = 1")
    suspend fun setAiEnabled(enabled: Boolean)

    @Query("UPDATE app_settings SET defaultGrade = :grade WHERE id = 1")
    suspend fun setDefaultGrade(grade: Int)

    @Query("UPDATE app_settings SET totalScore = totalScore + :points WHERE id = 1")
    suspend fun addScore(points: Int)
}
