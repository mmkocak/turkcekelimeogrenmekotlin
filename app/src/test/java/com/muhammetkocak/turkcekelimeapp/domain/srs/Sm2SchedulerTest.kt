package com.muhammetkocak.turkcekelimeapp.domain.srs

import com.muhammetkocak.turkcekelimeapp.core.datetime.DAY_MILLIS
import com.muhammetkocak.turkcekelimeapp.domain.model.CardMastery
import com.muhammetkocak.turkcekelimeapp.domain.model.SrsRating
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class Sm2SchedulerTest {

    private val scheduler = Sm2Scheduler()
    private val now = 1_700_000_000_000L

    private fun initial() = Sm2State(dueAt = now)

    @Test
    fun `first Good review schedules 1 day later and promotes to Learning`() {
        val result = scheduler.schedule(initial(), SrsRating.Good, now)
        assertEquals(1, result.intervalDays)
        assertEquals(1, result.repetition)
        assertEquals(now + DAY_MILLIS, result.dueAt)
        assertEquals(CardMastery.Learning, result.mastery)
        assertEquals(now, result.lastReviewedAt)
    }

    @Test
    fun `second Good review schedules 6 days later`() {
        val first = scheduler.schedule(initial(), SrsRating.Good, now)
        val second = scheduler.schedule(first, SrsRating.Good, now + DAY_MILLIS)
        assertEquals(6, second.intervalDays)
        assertEquals(2, second.repetition)
    }

    @Test
    fun `third Good review uses easiness to grow interval`() {
        var state = initial()
        state = scheduler.schedule(state, SrsRating.Good, now)
        state = scheduler.schedule(state, SrsRating.Good, now)
        val third = scheduler.schedule(state, SrsRating.Good, now)
        // Good preserves easiness (q=4 gives EF adjustment = 0.1 - 1*(0.08 + 0.02) = 0).
        // previous.intervalDays = 6, previous.easiness = 2.5 → 6 * 2.5 = 15
        assertEquals(15, third.intervalDays)
        assertEquals(3, third.repetition)
        assertEquals(CardMastery.Review, third.mastery)
    }

    @Test
    fun `Again resets repetition and schedules next day`() {
        var state = initial()
        repeat(3) { state = scheduler.schedule(state, SrsRating.Good, now) }
        val afterAgain = scheduler.schedule(state, SrsRating.Again, now)

        assertEquals(0, afterAgain.repetition)
        assertEquals(1, afterAgain.intervalDays)
        assertEquals(CardMastery.New, afterAgain.mastery)
        assertTrue(afterAgain.easiness < state.easiness)
    }

    @Test
    fun `Hard advances repetition but dampens interval relative to Good`() {
        var state = initial()
        state = scheduler.schedule(state, SrsRating.Good, now)
        state = scheduler.schedule(state, SrsRating.Good, now)
        val goodNext = scheduler.schedule(state, SrsRating.Good, now)
        val hardNext = scheduler.schedule(state, SrsRating.Hard, now)
        assertTrue(
            "Hard interval (${hardNext.intervalDays}) should be less than Good interval (${goodNext.intervalDays})",
            hardNext.intervalDays < goodNext.intervalDays
        )
        assertEquals(3, hardNext.repetition)
    }

    @Test
    fun `Easy grows interval more than Good`() {
        var state = initial()
        state = scheduler.schedule(state, SrsRating.Good, now)
        state = scheduler.schedule(state, SrsRating.Good, now)
        val goodNext = scheduler.schedule(state, SrsRating.Good, now)
        val easyNext = scheduler.schedule(state, SrsRating.Easy, now)
        assertTrue(
            "Easy interval (${easyNext.intervalDays}) should exceed Good interval (${goodNext.intervalDays})",
            easyNext.intervalDays > goodNext.intervalDays
        )
        assertTrue(easyNext.easiness >= state.easiness)
    }

    @Test
    fun `easiness never falls below 1_3`() {
        var state = initial()
        // Repeatedly tap Again to drive EF down.
        repeat(20) { state = scheduler.schedule(state, SrsRating.Again, now) }
        assertTrue(state.easiness >= Sm2Scheduler.MIN_EASINESS - 1e-9)
    }

    @Test
    fun `mastery reaches Mastered after enough successful Good reviews`() {
        var state = initial()
        repeat(Sm2Scheduler.MASTERED_MIN_REPETITIONS) {
            state = scheduler.schedule(state, SrsRating.Good, now)
        }
        assertEquals(CardMastery.Mastered, state.mastery)
        assertTrue(state.easiness >= Sm2Scheduler.MASTERED_MIN_EASINESS)
    }

    @Test
    fun `long chain of Hard prevents Mastery even with many repetitions`() {
        var state = initial()
        repeat(10) { state = scheduler.schedule(state, SrsRating.Hard, now) }
        assertTrue(state.easiness < Sm2Scheduler.MASTERED_MIN_EASINESS)
        // Easiness decays below 2.5 → stays in REVIEW, not MASTERED.
        assertEquals(CardMastery.Review, state.mastery)
    }

    @Test
    fun `dueAt is now plus interval in millis`() {
        val result = scheduler.schedule(initial(), SrsRating.Good, now)
        assertEquals(now + result.intervalDays * DAY_MILLIS, result.dueAt)
    }

    @Test
    fun `previewIntervalDays does not mutate input and returns same schedule interval`() {
        val state = initial().copy(repetition = 2, intervalDays = 6)
        val preview = scheduler.previewIntervalDays(state, SrsRating.Good)
        val applied = scheduler.schedule(state, SrsRating.Good, state.dueAt)
        assertEquals(applied.intervalDays, preview)
    }

    @Test
    fun `Good after single rep keeps promoting through Learning then Review`() {
        var state = initial()
        state = scheduler.schedule(state, SrsRating.Good, now)
        assertEquals(CardMastery.Learning, state.mastery)
        state = scheduler.schedule(state, SrsRating.Good, now)
        assertEquals(CardMastery.Learning, state.mastery)
        state = scheduler.schedule(state, SrsRating.Good, now)
        assertEquals(CardMastery.Review, state.mastery)
    }
}
