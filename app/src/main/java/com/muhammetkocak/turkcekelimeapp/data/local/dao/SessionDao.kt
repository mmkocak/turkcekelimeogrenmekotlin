package com.muhammetkocak.turkcekelimeapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.muhammetkocak.turkcekelimeapp.data.local.entity.SessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionDao {

    @Insert
    suspend fun insert(session: SessionEntity): Long

    @Query("""
        UPDATE sessions
        SET endedAt = :endedAt, correctCount = :correct, wrongCount = :wrong
        WHERE id = :id
    """)
    suspend fun finish(id: Long, endedAt: Long, correct: Int, wrong: Int)

    @Query("SELECT * FROM sessions ORDER BY startedAt DESC LIMIT :limit")
    fun observeRecent(limit: Int): Flow<List<SessionEntity>>
}
