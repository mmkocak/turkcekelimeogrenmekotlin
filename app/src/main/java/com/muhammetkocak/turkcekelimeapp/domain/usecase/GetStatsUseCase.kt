package com.muhammetkocak.turkcekelimeapp.domain.usecase

import com.muhammetkocak.turkcekelimeapp.core.datetime.Clock
import com.muhammetkocak.turkcekelimeapp.core.datetime.DAY_MILLIS
import com.muhammetkocak.turkcekelimeapp.core.datetime.startOfDayLocal
import com.muhammetkocak.turkcekelimeapp.data.local.dao.DailyReviewAggregate
import com.muhammetkocak.turkcekelimeapp.data.prefs.UserPreferencesDataStore
import com.muhammetkocak.turkcekelimeapp.data.repository.StatsRepository
import com.muhammetkocak.turkcekelimeapp.domain.model.CardMastery
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest

data class StatsSnapshot(
    val currentStreak: Int,
    val longestStreak: Int,
    val masteryCounts: Map<CardMastery, Int>,
    val weeklyAggregates: List<DailyReviewAggregate>,
    val windowStartEpoch: Long,
    val windowEndEpoch: Long
) {
    val totalCards: Int get() = masteryCounts.values.sum()
    val weeklyTotalReviews: Int get() = weeklyAggregates.sumOf { it.reviewCount }
}

class GetStatsUseCase @Inject constructor(
    private val preferencesDataStore: UserPreferencesDataStore,
    private val statsRepository: StatsRepository,
    private val clock: Clock
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(): Flow<StatsSnapshot> =
        preferencesDataStore.preferences.flatMapLatest { prefs ->
            val now = clock.nowMillis()
            val windowEnd = startOfDayLocal(now) + DAY_MILLIS
            val windowStart = windowEnd - 42L * DAY_MILLIS // son 6 hafta heatmap için
            combine(
                statsRepository.observeMasteryCounts(prefs.primaryDirection),
                statsRepository.observeDailyAggregates(windowStart, windowEnd)
            ) { masteryList, aggregates ->
                val masteryMap = CardMastery.entries.associateWith { 0 }.toMutableMap()
                masteryList.forEach { row ->
                    masteryMap[CardMastery.fromRaw(row.mastery)] = row.total
                }
                StatsSnapshot(
                    currentStreak = prefs.currentStreak,
                    longestStreak = prefs.longestStreak,
                    masteryCounts = masteryMap,
                    weeklyAggregates = aggregates,
                    windowStartEpoch = windowStart,
                    windowEndEpoch = windowEnd
                )
            }
        }
}
