package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.QuizResultEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface QuizDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuizResult(result: QuizResultEntity): Long

    @Query("SELECT * FROM quiz_results ORDER BY timestamp DESC")
    fun getAllResults(): Flow<List<QuizResultEntity>>

    @Query("SELECT * FROM quiz_results ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentResults(limit: Int): Flow<List<QuizResultEntity>>

    @Query("SELECT COUNT(*) FROM quiz_results")
    fun getQuizCount(): Flow<Int>

    @Query("SELECT AVG(percentage) FROM quiz_results")
    fun getAveragePercentage(): Flow<Double?>

    @Query("SELECT SUM(correctCount) FROM quiz_results")
    fun getTotalCorrectCount(): Flow<Int?>

    @Query("SELECT SUM(wrongCount) FROM quiz_results")
    fun getTotalWrongCount(): Flow<Int?>

    @Query("SELECT * FROM quiz_results WHERE id = :id")
    suspend fun getResultById(id: Long): QuizResultEntity?

    @Query("DELETE FROM quiz_results WHERE id = :id")
    suspend fun deleteResultById(id: Long)
}
