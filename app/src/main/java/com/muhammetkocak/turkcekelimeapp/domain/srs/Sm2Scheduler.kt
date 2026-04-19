package com.muhammetkocak.turkcekelimeapp.domain.srs

import com.muhammetkocak.turkcekelimeapp.core.datetime.DAY_MILLIS
import com.muhammetkocak.turkcekelimeapp.domain.model.CardMastery
import com.muhammetkocak.turkcekelimeapp.domain.model.SrsRating
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.max
import kotlin.math.roundToInt

/**
 * SM-2 algoritmasının saf (stateless) uygulaması.
 * SuperMemo-2 tabanlı, Anki benzeri Again/Hard/Good/Easy butonları ile genişletilmiş.
 */
@Singleton
class Sm2Scheduler @Inject constructor() {

    fun schedule(previous: Sm2State, rating: SrsRating, now: Long): Sm2State {
        val q = rating.quality
        val newEasiness = (previous.easiness + 0.1 - (5 - q) * (0.08 + (5 - q) * 0.02))
            .coerceAtLeast(MIN_EASINESS)

        val (newRepetition, newIntervalDays) = if (rating == SrsRating.Again) {
            0 to 1
        } else {
            val advancedRepetition = previous.repetition + 1
            val baseInterval = when (previous.repetition) {
                0 -> FIRST_INTERVAL_DAYS
                1 -> SECOND_INTERVAL_DAYS
                else -> (previous.intervalDays * previous.easiness).roundToInt().coerceAtLeast(1)
            }
            val modified = when (rating) {
                SrsRating.Hard -> (baseInterval * HARD_MULTIPLIER).roundToInt().coerceAtLeast(1)
                SrsRating.Easy -> (baseInterval * EASY_MULTIPLIER).roundToInt().coerceAtLeast(baseInterval + 1)
                else -> baseInterval
            }
            advancedRepetition to max(1, modified)
        }

        val newDueAt = now + newIntervalDays * DAY_MILLIS
        val newMastery = computeMastery(newRepetition, newEasiness)

        return Sm2State(
            easiness = newEasiness,
            intervalDays = newIntervalDays,
            repetition = newRepetition,
            dueAt = newDueAt,
            lastReviewedAt = now,
            mastery = newMastery
        )
    }

    /**
     * SM-2 mantığını uygulamadan bir sonraki interval'i önizler (UI'da rating butonlarının altında gösterilir).
     */
    fun previewIntervalDays(previous: Sm2State, rating: SrsRating): Int =
        schedule(previous, rating, now = previous.dueAt).intervalDays

    private fun computeMastery(repetition: Int, easiness: Double): CardMastery = when {
        repetition == 0 -> CardMastery.New
        repetition in 1..2 -> CardMastery.Learning
        repetition >= MASTERED_MIN_REPETITIONS && easiness >= MASTERED_MIN_EASINESS -> CardMastery.Mastered
        else -> CardMastery.Review
    }

    companion object {
        const val MIN_EASINESS: Double = 1.3
        const val FIRST_INTERVAL_DAYS: Int = 1
        const val SECOND_INTERVAL_DAYS: Int = 6
        const val HARD_MULTIPLIER: Double = 0.6
        const val EASY_MULTIPLIER: Double = 1.3
        const val MASTERED_MIN_REPETITIONS: Int = 7
        const val MASTERED_MIN_EASINESS: Double = 2.5
    }
}
