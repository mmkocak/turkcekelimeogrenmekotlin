package com.muhammetkocak.turkcekelimeapp.ui.root

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.muhammetkocak.turkcekelimeapp.data.prefs.UserPreferences
import com.muhammetkocak.turkcekelimeapp.data.prefs.UserPreferencesDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class AppRootState(val preferences: UserPreferences? = null)

@HiltViewModel
class AppRootViewModel @Inject constructor(
    preferencesDataStore: UserPreferencesDataStore
) : ViewModel() {
    val state: StateFlow<AppRootState> = preferencesDataStore.preferences
        .map { AppRootState(preferences = it) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, AppRootState())
}
