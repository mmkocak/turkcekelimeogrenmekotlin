package com.muhammetkocak.turkcekelimeapp.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.muhammetkocak.turkcekelimeapp.data.prefs.UserPreferences
import com.muhammetkocak.turkcekelimeapp.data.repository.WordRepository
import com.muhammetkocak.turkcekelimeapp.domain.model.Category
import com.muhammetkocak.turkcekelimeapp.domain.usecase.GetHomeSummaryUseCase
import com.muhammetkocak.turkcekelimeapp.domain.usecase.HomeSummary
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class HomeUiState(
    val loading: Boolean = true,
    val summary: HomeSummary? = null,
    val categories: List<Category> = emptyList(),
    val preferences: UserPreferences = UserPreferences.Default
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    getHomeSummary: GetHomeSummaryUseCase,
    wordRepository: WordRepository
) : ViewModel() {

    @OptIn(ExperimentalCoroutinesApi::class)
    val state: StateFlow<HomeUiState> =
        combine(
            getHomeSummary(),
            wordRepository.observeCategories()
        ) { summary, categories ->
            HomeUiState(
                loading = false,
                summary = summary,
                categories = categories,
                preferences = summary.preferences
            )
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            HomeUiState()
        )
}
