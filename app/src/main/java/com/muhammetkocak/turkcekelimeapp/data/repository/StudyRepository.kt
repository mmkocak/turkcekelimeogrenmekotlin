package com.muhammetkocak.turkcekelimeapp.data.repository

import com.muhammetkocak.turkcekelimeapp.data.local.dao.CardStateDao
import com.muhammetkocak.turkcekelimeapp.data.local.dao.DueCardView
import com.muhammetkocak.turkcekelimeapp.data.local.dao.ReviewDao
import com.muhammetkocak.turkcekelimeapp.data.local.dao.SessionDao
import com.muhammetkocak.turkcekelimeapp.data.local.entity.CardStateEntity
import com.muhammetkocak.turkcekelimeapp.data.local.entity.ReviewEntity
import com.muhammetkocak.turkcekelimeapp.data.local.entity.SessionEntity
import com.muhammetkocak.turkcekelimeapp.data.mapper.toStudyCard
import com.muhammetkocak.turkcekelimeapp.di.IoDispatcher
import com.muhammetkocak.turkcekelimeapp.domain.model.LearningDirection
import com.muhammetkocak.turkcekelimeapp.domain.model.StudyCard
import com.muhammetkocak.turkcekelimeapp.domain.srs.Sm2State
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

@Singleton
class StudyRepository @Inject constructor(
    private val cardStateDao: CardStateDao,
    private val reviewDao: ReviewDao,
    private val sessionDao: SessionDao,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {

    fun observeDueCount(direction: LearningDirection, now: Long): Flow<Int> =
        cardStateDao.observeDueCount(now, direction.raw)

    suspend fun getDueCount(direction: LearningDirection, now: Long): Int = withContext(ioDispatcher) {
        cardStateDao.getDueCount(now, direction.raw)
    }

    suspend fun getDueCards(
        direction: LearningDirection,
        now: Long,
        limit: Int,
        categoryId: Long? = null
    ): List<StudyCard> = withContext(ioDispatcher) {
        cardStateDao.getDueCards(now, direction.raw, limit, categoryId).map(DueCardView::toStudyCard)
    }

    suspend fun getCardState(wordId: Long, direction: LearningDirection): Sm2State? = withContext(ioDispatcher) {
        cardStateDao.getState(wordId, direction.raw)?.let {
            Sm2State(
                easiness = it.easiness,
                intervalDays = it.intervalDays,
                repetition = it.repetition,
                dueAt = it.dueAt,
                lastReviewedAt = it.lastReviewedAt,
                mastery = com.muhammetkocak.turkcekelimeapp.domain.model.CardMastery.fromRaw(it.mastery)
            )
        }
    }

    suspend fun upsertCardState(wordId: Long, direction: LearningDirection, state: Sm2State) = withContext(ioDispatcher) {
        cardStateDao.upsert(
            CardStateEntity(
                wordId = wordId,
                direction = direction.raw,
                easiness = state.easiness,
                intervalDays = state.intervalDays,
                repetition = state.repetition,
                dueAt = state.dueAt,
                lastReviewedAt = state.lastReviewedAt,
                mastery = state.mastery.raw
            )
        )
    }

    suspend fun logReview(review: ReviewEntity): Long = withContext(ioDispatcher) {
        reviewDao.insert(review)
    }

    suspend fun startSession(mode: String, startedAt: Long): Long = withContext(ioDispatcher) {
        sessionDao.insert(SessionEntity(mode = mode, startedAt = startedAt))
    }

    suspend fun finishSession(id: Long, endedAt: Long, correct: Int, wrong: Int) = withContext(ioDispatcher) {
        sessionDao.finish(id, endedAt, correct, wrong)
    }
}
