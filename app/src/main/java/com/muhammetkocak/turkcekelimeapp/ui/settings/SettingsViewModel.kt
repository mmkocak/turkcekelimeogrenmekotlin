package com.muhammetkocak.turkcekelimeapp.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.muhammetkocak.turkcekelimeapp.data.prefs.UserPreferences
import com.muhammetkocak.turkcekelimeapp.data.prefs.UserPreferencesDataStore
import com.muhammetkocak.turkcekelimeapp.domain.model.LearningDirection
import com.muhammetkocak.turkcekelimeapp.ui.theme.ThemeMode
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val prefs: UserPreferencesDataStore
) : ViewModel() {

    val state: StateFlow<UserPreferences> = prefs.preferences
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UserPreferences.Default)

    fun setThemeMode(mode: ThemeMode) = viewModelScope.launch { prefs.setThemeMode(mode) }
    fun setDynamicColor(enabled: Boolean) = viewModelScope.launch { prefs.setDynamicColor(enabled) }
    fun setDirection(direction: LearningDirection) = viewModelScope.launch { prefs.setPrimaryDirection(direction) }
    fun setDailyGoal(value: Int) = viewModelScope.launch { prefs.setDailyGoal(value) }
    fun setTtsEnabled(enabled: Boolean) = viewModelScope.launch { prefs.setTtsEnabled(enabled) }
    fun setHapticsEnabled(enabled: Boolean) = viewModelScope.launch { prefs.setHapticsEnabled(enabled) }
    fun resetProgress() = viewModelScope.launch { prefs.resetProgress() }
}
