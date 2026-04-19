package com.muhammetkocak.turkcekelimeapp.domain.usecase

import com.muhammetkocak.turkcekelimeapp.core.datetime.Clock
import com.muhammetkocak.turkcekelimeapp.core.datetime.DAY_MILLIS
import com.muhammetkocak.turkcekelimeapp.core.datetime.startOfDayLocal
import com.muhammetkocak.turkcekelimeapp.data.prefs.UserPreferences
import com.muhammetkocak.turkcekelimeapp.data.prefs.UserPreferencesDataStore
import com.muhammetkocak.turkcekelimeapp.data.repository.StudyRepository
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest

data class HomeSummary(
    val dueCount: Int,
    val todayReviewCount: Int,
    val dailyGoal: Int,
    val currentStreak: Int,
    val longestStreak: Int,
    val preferences: UserPreferences
) {
    val goalProgress: Float
        get() = if (dailyGoal <= 0) 0f else (todayReviewCount.toFloat() / dailyGoal).coerceIn(0f, 1f)
}

/**
 * Home ekranının ihtiyacı olan tüm özetleri tek bir Flow'da birleştirir.
 */
class GetHomeSummaryUseCase @Inject constructor(
    private val preferencesDataStore: UserPreferencesDataStore,
    private val studyRepository: StudyRepository,
    private val statsRepository: com.muhammetkocak.turkcekelimeapp.data.repository.StatsRepository,
    private val clock: Clock
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(): Flow<HomeSummary> =
        preferencesDataStore.preferences.flatMapLatest { prefs ->
            val now = clock.nowMillis()
            val dayStart = startOfDayLocal(now)
            val dayEnd = dayStart + DAY_MILLIS
            combine(
                studyRepository.observeDueCount(prefs.primaryDirection, now),
                statsRepository.observeReviewsInRange(dayStart, dayEnd)
            ) { dueCount, todayReviews ->
                HomeSummary(
                    dueCount = dueCount,
                    todayReviewCount = todayReviews,
                    dailyGoal = prefs.dailyGoal,
                    currentStreak = prefs.currentStreak,
                    longestStreak = prefs.longestStreak,
                    preferences = prefs
                )
            }
        }
}
