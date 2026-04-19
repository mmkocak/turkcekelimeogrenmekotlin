package com.muhammetkocak.turkcekelimeapp.data.prefs

import com.muhammetkocak.turkcekelimeapp.domain.model.LearningDirection
import com.muhammetkocak.turkcekelimeapp.ui.theme.ThemeMode

/**
 * Kullanıcı tercihleri. DataStore içinde kalıcı tutulur.
 */
data class UserPreferences(
    val themeMode: ThemeMode,
    val dynamicColorEnabled: Boolean,
    val primaryDirection: LearningDirection,
    val dailyGoal: Int,
    val ttsEnabled: Boolean,
    val hapticsEnabled: Boolean,
    val firstRunCompleted: Boolean,
    val currentStreak: Int,
    val longestStreak: Int,
    val lastStudyDayEpoch: Long
) {
    companion object {
        val Default = UserPreferences(
            themeMode = ThemeMode.System,
            dynamicColorEnabled = true,
            primaryDirection = LearningDirection.ForeignToTurkish,
            dailyGoal = 20,
            ttsEnabled = true,
            hapticsEnabled = true,
            firstRunCompleted = false,
            currentStreak = 0,
            longestStreak = 0,
            lastStudyDayEpoch = 0L
        )
    }
}
