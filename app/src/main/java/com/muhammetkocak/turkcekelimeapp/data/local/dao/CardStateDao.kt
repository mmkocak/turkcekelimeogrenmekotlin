package com.muhammetkocak.turkcekelimeapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.muhammetkocak.turkcekelimeapp.data.local.entity.CardStateEntity
import com.muhammetkocak.turkcekelimeapp.data.local.entity.WordEntity
import kotlinx.coroutines.flow.Flow

data class DueCardView(
    val wordId: Long,
    val direction: String,
    val easiness: Double,
    val intervalDays: Int,
    val repetition: Int,
    val dueAt: Long,
    val lastReviewedAt: Long?,
    val mastery: String,
    val foreignTerm: String,
    val turkishTerm: String,
    val partOfSpeech: String?,
    val exampleForeign: String?,
    val exampleTurkish: String?,
    val ipa: String?,
    val categoryId: Long,
    val isFavorite: Boolean
)

data class MasteryCount(
    val mastery: String,
    val total: Int
)

@Dao
interface CardStateDao {

    @Query("""
        SELECT COUNT(*) FROM card_state
        WHERE dueAt <= :now AND direction = :direction
    """)
    fun observeDueCount(now: Long, direction: String): Flow<Int>

    @Query("""
        SELECT COUNT(*) FROM card_state WHERE dueAt <= :now AND direction = :direction
    """)
    suspend fun getDueCount(now: Long, direction: String): Int

    @Query("""
        SELECT cs.wordId AS wordId,
               cs.direction AS direction,
               cs.easiness AS easiness,
               cs.intervalDays AS intervalDays,
               cs.repetition AS repetition,
               cs.dueAt AS dueAt,
               cs.lastReviewedAt AS lastReviewedAt,
               cs.mastery AS mastery,
               w.foreignTerm AS foreignTerm,
               w.turkishTerm AS turkishTerm,
               w.partOfSpeech AS partOfSpeech,
               w.exampleForeign AS exampleForeign,
               w.exampleTurkish AS exampleTurkish,
               w.ipa AS ipa,
               w.categoryId AS categoryId,
               w.isFavorite AS isFavorite
        FROM card_state cs
        INNER JOIN words w ON w.id = cs.wordId
        WHERE cs.direction = :direction
          AND cs.dueAt <= :now
          AND (:categoryId IS NULL OR w.categoryId = :categoryId)
        ORDER BY cs.dueAt ASC, RANDOM()
        LIMIT :limit
    """)
    suspend fun getDueCards(
        now: Long,
        direction: String,
        limit: Int,
        categoryId: Long?
    ): List<DueCardView>

    @Query("SELECT * FROM card_state WHERE wordId = :wordId AND direction = :direction")
    suspend fun getState(wordId: Long, direction: String): CardStateEntity?

    @Upsert
    suspend fun upsert(state: CardStateEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(states: List<CardStateEntity>)

    @Query("SELECT mastery AS mastery, COUNT(*) AS total FROM card_state WHERE direction = :direction GROUP BY mastery")
    fun observeMasteryCounts(direction: String): Flow<List<MasteryCount>>

    @Query("""
        SELECT COUNT(*) FROM card_state cs
        INNER JOIN words w ON w.id = cs.wordId
        WHERE w.categoryId = :categoryId AND cs.direction = :direction AND cs.mastery = 'MASTERED'
    """)
    fun observeMasteredCountForCategory(categoryId: Long, direction: String): Flow<Int>

    @Query("SELECT DISTINCT wordId FROM card_state WHERE mastery = 'MASTERED'")
    fun observeMasteredWordIds(): Flow<List<Long>>
}
