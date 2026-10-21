package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.MistakeNoteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MistakeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMistake(mistake: MistakeNoteEntity): Long

    @Query("SELECT * FROM mistake_notes ORDER BY timestamp DESC")
    fun getAllMistakes(): Flow<List<MistakeNoteEntity>>

    @Query("SELECT * FROM mistake_notes WHERE isResolved = 0 ORDER BY timestamp DESC")
    fun getUnresolvedMistakes(): Flow<List<MistakeNoteEntity>>

    @Query("SELECT COUNT(*) FROM mistake_notes WHERE isResolved = 0")
    fun getUnresolvedMistakesCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM mistake_notes")
    fun getTotalMistakesCount(): Flow<Int>

    @Query("UPDATE mistake_notes SET isResolved = :isResolved WHERE id = :id")
    suspend fun updateResolvedStatus(id: Long, isResolved: Boolean)

    @Query("UPDATE mistake_notes SET userNote = :note WHERE id = :id")
    suspend fun updateUserNote(id: Long, note: String)

    @Query("DELETE FROM mistake_notes WHERE id = :id")
    suspend fun deleteMistakeById(id: Long)
}
