package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.MathQuestionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface QuestionDao {
    @Query("SELECT * FROM math_questions ORDER BY grade ASC, id ASC")
    fun getAllQuestions(): Flow<List<MathQuestionEntity>>

    @Query("SELECT COUNT(*) FROM math_questions")
    suspend fun getQuestionsCount(): Int

    @Query("SELECT * FROM math_questions WHERE grade = :grade ORDER BY id ASC")
    fun getQuestionsByGrade(grade: Int): Flow<List<MathQuestionEntity>>

    @Query("""
        SELECT * FROM math_questions 
        WHERE grade = :grade 
        AND (:topic IS NULL OR topic = :topic)
        AND (:difficulty IS NULL OR difficulty = :difficulty)
        ORDER BY RANDOM() 
        LIMIT :limit
    """)
    suspend fun getQuizQuestions(grade: Int, topic: String?, difficulty: String?, limit: Int): List<MathQuestionEntity>

    @Query("SELECT DISTINCT topic FROM math_questions WHERE grade = :grade ORDER BY topic ASC")
    suspend fun getTopicsByGrade(grade: Int): List<String>

    @Query("SELECT * FROM math_questions WHERE id = :id LIMIT 1")
    suspend fun getQuestionById(id: String): MathQuestionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestions(questions: List<MathQuestionEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestion(question: MathQuestionEntity)
}
