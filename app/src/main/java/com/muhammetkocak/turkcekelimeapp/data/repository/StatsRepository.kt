package com.muhammetkocak.turkcekelimeapp.data.repository

import com.muhammetkocak.turkcekelimeapp.data.local.dao.CardStateDao
import com.muhammetkocak.turkcekelimeapp.data.local.dao.DailyReviewAggregate
import com.muhammetkocak.turkcekelimeapp.data.local.dao.MasteryCount
import com.muhammetkocak.turkcekelimeapp.data.local.dao.ReviewDao
import com.muhammetkocak.turkcekelimeapp.data.local.dao.SessionDao
import com.muhammetkocak.turkcekelimeapp.data.local.entity.SessionEntity
import com.muhammetkocak.turkcekelimeapp.di.IoDispatcher
import com.muhammetkocak.turkcekelimeapp.domain.model.LearningDirection
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

@Singleton
class StatsRepository @Inject constructor(
    private val reviewDao: ReviewDao,
    private val cardStateDao: CardStateDao,
    private val sessionDao: SessionDao,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {

    fun observeReviewsInRange(from: Long, to: Long): Flow<Int> =
        reviewDao.observeCountInRange(from, to)

    fun observeDailyAggregates(from: Long, to: Long): Flow<List<DailyReviewAggregate>> =
        reviewDao.observeDailyAggregates(from, to)

    fun observeMasteryCounts(direction: LearningDirection): Flow<List<MasteryCount>> =
        cardStateDao.observeMasteryCounts(direction.raw)

    fun observeRecentSessions(limit: Int): Flow<List<SessionEntity>> =
        sessionDao.observeRecent(limit)

    suspend fun totalReviews(): Int = withContext(ioDispatcher) {
        reviewDao.totalCount()
    }
}
