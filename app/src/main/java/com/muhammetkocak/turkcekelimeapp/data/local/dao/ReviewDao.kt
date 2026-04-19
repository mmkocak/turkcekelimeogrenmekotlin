package com.muhammetkocak.turkcekelimeapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.muhammetkocak.turkcekelimeapp.data.local.entity.ReviewEntity
import kotlinx.coroutines.flow.Flow

data class DailyReviewAggregate(
    val dayEpoch: Long,
    val reviewCount: Int,
    val correctCount: Int
)

@Dao
interface ReviewDao {

    @Insert
    suspend fun insert(review: ReviewEntity): Long

    @Query("SELECT COUNT(*) FROM reviews WHERE reviewedAt >= :from AND reviewedAt < :to")
    fun observeCountInRange(from: Long, to: Long): Flow<Int>

    @Query("SELECT COUNT(*) FROM reviews WHERE reviewedAt >= :from AND reviewedAt < :to")
    suspend fun countInRange(from: Long, to: Long): Int

    @Query("""
        SELECT
          (reviewedAt / 86400000) * 86400000 AS dayEpoch,
          COUNT(*) AS reviewCount,
          SUM(CASE WHEN rating >= 3 THEN 1 ELSE 0 END) AS correctCount
        FROM reviews
        WHERE reviewedAt >= :from AND reviewedAt < :to
        GROUP BY dayEpoch
        ORDER BY dayEpoch ASC
    """)
    fun observeDailyAggregates(from: Long, to: Long): Flow<List<DailyReviewAggregate>>

    @Query("SELECT COUNT(*) FROM reviews")
    suspend fun totalCount(): Int
}
