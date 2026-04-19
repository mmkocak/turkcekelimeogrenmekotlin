package com.muhammetkocak.turkcekelimeapp.data.prefs

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.muhammetkocak.turkcekelimeapp.domain.model.LearningDirection
import com.muhammetkocak.turkcekelimeapp.ui.theme.ThemeMode
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

@Singleton
class UserPreferencesDataStore @Inject constructor(
    @dagger.hilt.android.qualifiers.ApplicationContext private val context: Context
) {

    val preferences: Flow<UserPreferences> = context.dataStore.data.map { prefs ->
        UserPreferences(
            themeMode = prefs[Keys.ThemeMode]
                ?.let { runCatching { ThemeMode.valueOf(it) }.getOrNull() }
                ?: ThemeMode.System,
            dynamicColorEnabled = prefs[Keys.DynamicColor] ?: true,
            primaryDirection = prefs[Keys.PrimaryDirection]
                ?.let { LearningDirection.fromRaw(it) }
                ?: LearningDirection.ForeignToTurkish,
            dailyGoal = prefs[Keys.DailyGoal] ?: 20,
            ttsEnabled = prefs[Keys.TtsEnabled] ?: true,
            hapticsEnabled = prefs[Keys.HapticsEnabled] ?: true,
            firstRunCompleted = prefs[Keys.FirstRunCompleted] ?: false,
            currentStreak = prefs[Keys.CurrentStreak] ?: 0,
            longestStreak = prefs[Keys.LongestStreak] ?: 0,
            lastStudyDayEpoch = prefs[Keys.LastStudyDayEpoch] ?: 0L
        )
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        context.dataStore.edit { it[Keys.ThemeMode] = mode.name }
    }

    suspend fun setDynamicColor(enabled: Boolean) {
        context.dataStore.edit { it[Keys.DynamicColor] = enabled }
    }

    suspend fun setPrimaryDirection(direction: LearningDirection) {
        context.dataStore.edit { it[Keys.PrimaryDirection] = direction.raw }
    }

    suspend fun setDailyGoal(value: Int) {
        context.dataStore.edit { it[Keys.DailyGoal] = value.coerceIn(5, 200) }
    }

    suspend fun setTtsEnabled(enabled: Boolean) {
        context.dataStore.edit { it[Keys.TtsEnabled] = enabled }
    }

    suspend fun setHapticsEnabled(enabled: Boolean) {
        context.dataStore.edit { it[Keys.HapticsEnabled] = enabled }
    }

    suspend fun markFirstRunCompleted() {
        context.dataStore.edit { it[Keys.FirstRunCompleted] = true }
    }

    suspend fun updateStreak(current: Int, longest: Int, lastStudyDayEpoch: Long) {
        context.dataStore.edit {
            it[Keys.CurrentStreak] = current
            it[Keys.LongestStreak] = longest
            it[Keys.LastStudyDayEpoch] = lastStudyDayEpoch
        }
    }

    suspend fun resetProgress() {
        context.dataStore.edit { prefs ->
            prefs[Keys.CurrentStreak] = 0
            prefs[Keys.LongestStreak] = 0
            prefs[Keys.LastStudyDayEpoch] = 0L
        }
    }

    private object Keys {
        val ThemeMode = stringPreferencesKey("theme_mode")
        val DynamicColor = booleanPreferencesKey("dynamic_color")
        val PrimaryDirection = stringPreferencesKey("primary_direction")
        val DailyGoal = intPreferencesKey("daily_goal")
        val TtsEnabled = booleanPreferencesKey("tts_enabled")
        val HapticsEnabled = booleanPreferencesKey("haptics_enabled")
        val FirstRunCompleted = booleanPreferencesKey("first_run_completed")
        val CurrentStreak = intPreferencesKey("current_streak")
        val LongestStreak = intPreferencesKey("longest_streak")
        val LastStudyDayEpoch = longPreferencesKey("last_study_day_epoch")
    }
}
