package com.muhammetkocak.turkcekelimeapp.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.muhammetkocak.turkcekelimeapp.data.prefs.UserPreferencesDataStore
import com.muhammetkocak.turkcekelimeapp.domain.model.LearningDirection
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val prefs: UserPreferencesDataStore
) : ViewModel() {

    fun finish(direction: LearningDirection, dailyGoal: Int, onDone: () -> Unit) {
        viewModelScope.launch {
            prefs.setPrimaryDirection(direction)
            prefs.setDailyGoal(dailyGoal)
            prefs.markFirstRunCompleted()
            onDone()
        }
    }
}
