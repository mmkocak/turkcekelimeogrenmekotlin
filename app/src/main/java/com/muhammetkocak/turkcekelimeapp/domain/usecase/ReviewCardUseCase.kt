package com.muhammetkocak.turkcekelimeapp.domain.usecase

import com.muhammetkocak.turkcekelimeapp.core.datetime.Clock
import com.muhammetkocak.turkcekelimeapp.core.datetime.daysBetween
import com.muhammetkocak.turkcekelimeapp.core.datetime.startOfDayLocal
import com.muhammetkocak.turkcekelimeapp.data.local.entity.ReviewEntity
import com.muhammetkocak.turkcekelimeapp.data.prefs.UserPreferencesDataStore
import com.muhammetkocak.turkcekelimeapp.data.repository.StudyRepository
import com.muhammetkocak.turkcekelimeapp.domain.model.LearningDirection
import com.muhammetkocak.turkcekelimeapp.domain.model.SrsRating
import com.muhammetkocak.turkcekelimeapp.domain.srs.Sm2Scheduler
import com.muhammetkocak.turkcekelimeapp.domain.srs.Sm2State
import javax.inject.Inject
import kotlinx.coroutines.flow.first

/**
 * Kullanıcı kart oylandığında çağrılır: SM-2 uygular, yeni state'i yazar, review'i loglar,
 * günlük streak'i günceller. Geri dönüşte bir sonraki SM-2 state'i verir.
 */
class ReviewCardUseCase @Inject constructor(
    private val studyRepository: StudyRepository,
    private val preferences: UserPreferencesDataStore,
    private val scheduler: Sm2Scheduler,
    private val clock: Clock
) {
    suspend operator fun invoke(
        wordId: Long,
        direction: LearningDirection,
        rating: SrsRating,
        sessionId: Long? = null
    ): Sm2State {
        val now = clock.nowMillis()
        val previous = studyRepository.getCardState(wordId, direction)
            ?: Sm2State(dueAt = now)

        val next = scheduler.schedule(previous, rating, now)
        studyRepository.upsertCardState(wordId, direction, next)
        studyRepository.logReview(
            ReviewEntity(
                wordId = wordId,
                direction = direction.raw,
                rating = rating.quality,
                reviewedAt = now,
                previousIntervalDays = previous.intervalDays,
                nextIntervalDays = next.intervalDays,
                sessionId = sessionId
            )
        )
        updateStreak(now)
        return next
    }

    private suspend fun updateStreak(now: Long) {
        val prefs = preferences.preferences.first()
        val today = startOfDayLocal(now)
        if (prefs.lastStudyDayEpoch == today) return

        val newCurrent = when {
            prefs.lastStudyDayEpoch == 0L -> 1
            daysBetween(prefs.lastStudyDayEpoch, today) == 1L -> prefs.currentStreak + 1
            else -> 1
        }
        val newLongest = maxOf(prefs.longestStreak, newCurrent)
        preferences.updateStreak(newCurrent, newLongest, today)
    }
}
