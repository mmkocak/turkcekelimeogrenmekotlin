package com.muhammetkocak.turkcekelimeapp.ui.study.session

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.muhammetkocak.turkcekelimeapp.core.datetime.Clock
import com.muhammetkocak.turkcekelimeapp.data.prefs.UserPreferencesDataStore
import com.muhammetkocak.turkcekelimeapp.data.repository.StudyRepository
import com.muhammetkocak.turkcekelimeapp.domain.model.LearningDirection
import com.muhammetkocak.turkcekelimeapp.domain.model.SrsRating
import com.muhammetkocak.turkcekelimeapp.domain.model.StudyCard
import com.muhammetkocak.turkcekelimeapp.domain.model.StudyMode
import com.muhammetkocak.turkcekelimeapp.domain.srs.Sm2Scheduler
import com.muhammetkocak.turkcekelimeapp.domain.usecase.BuildQuizOptionsUseCase
import com.muhammetkocak.turkcekelimeapp.domain.usecase.GetDueCardsUseCase
import com.muhammetkocak.turkcekelimeapp.domain.usecase.QuizOptions
import com.muhammetkocak.turkcekelimeapp.domain.usecase.ReviewCardUseCase
import com.muhammetkocak.turkcekelimeapp.navigation.Screen
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SessionSummary(
    val mode: StudyMode,
    val direction: LearningDirection,
    val correct: Int,
    val wrong: Int,
    val totalReviewed: Int
)

data class StudyUiState(
    val mode: StudyMode = StudyMode.Flashcard,
    val direction: LearningDirection = LearningDirection.ForeignToTurkish,
    val loading: Boolean = true,
    val queue: List<StudyCard> = emptyList(),
    val currentIndex: Int = 0,
    val options: QuizOptions? = null,
    val correct: Int = 0,
    val wrong: Int = 0,
    val finished: Boolean = false,
    val summary: SessionSummary? = null,
    val previewIntervals: Map<SrsRating, Int> = emptyMap()
) {
    val currentCard: StudyCard? get() = queue.getOrNull(currentIndex)
    val progress: Float get() = if (queue.isEmpty()) 0f else currentIndex.toFloat() / queue.size
}

@HiltViewModel
class StudySessionViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getDueCards: GetDueCardsUseCase,
    private val reviewCard: ReviewCardUseCase,
    private val buildQuizOptions: BuildQuizOptionsUseCase,
    private val studyRepository: StudyRepository,
    private val preferences: UserPreferencesDataStore,
    private val scheduler: Sm2Scheduler,
    private val clock: Clock
) : ViewModel() {

    private val route: Screen.Study = savedStateHandle.toRoute()
    private val mode: StudyMode = StudyMode.fromRaw(route.mode)
    private val direction: LearningDirection = LearningDirection.fromRaw(route.direction)
    private val categoryId: Long? = route.categoryId

    private val _state = MutableStateFlow(StudyUiState(mode = mode, direction = direction))
    val state: StateFlow<StudyUiState> = _state.asStateFlow()

    private var sessionId: Long? = null

    init {
        start()
    }

    private fun start() {
        viewModelScope.launch {
            val goal = preferences.preferences.first().dailyGoal
            val limit = goal.coerceAtLeast(10)
            val cards = getDueCards(direction = direction, limit = limit, categoryId = categoryId)
            val now = clock.nowMillis()
            sessionId = studyRepository.startSession(mode.raw, now)
            if (cards.isEmpty()) {
                _state.update {
                    it.copy(
                        loading = false,
                        queue = emptyList(),
                        finished = true,
                        summary = SessionSummary(mode, direction, 0, 0, 0)
                    )
                }
                return@launch
            }
            val first = cards.first()
            val previews = computePreviews(first)
            val options = if (mode != StudyMode.Typing) buildQuizOptions(first.word, direction) else null
            _state.update {
                it.copy(
                    loading = false,
                    queue = cards,
                    currentIndex = 0,
                    options = options,
                    previewIntervals = previews
                )
            }
        }
    }

    fun submitRating(rating: SrsRating, wasCorrect: Boolean) {
        val snapshot = _state.value
        val card = snapshot.currentCard ?: return
        viewModelScope.launch {
            reviewCard(card.word.id, card.direction, rating, sessionId)
            val newCorrect = snapshot.correct + if (wasCorrect) 1 else 0
            val newWrong = snapshot.wrong + if (!wasCorrect) 1 else 0
            val nextIndex = snapshot.currentIndex + 1
            if (nextIndex >= snapshot.queue.size) {
                val now = clock.nowMillis()
                sessionId?.let { studyRepository.finishSession(it, now, newCorrect, newWrong) }
                _state.update {
                    it.copy(
                        currentIndex = nextIndex,
                        correct = newCorrect,
                        wrong = newWrong,
                        finished = true,
                        summary = SessionSummary(mode, direction, newCorrect, newWrong, newCorrect + newWrong)
                    )
                }
            } else {
                val nextCard = snapshot.queue[nextIndex]
                val previews = computePreviews(nextCard)
                val options = if (mode != StudyMode.Typing) buildQuizOptions(nextCard.word, direction) else null
                _state.update {
                    it.copy(
                        currentIndex = nextIndex,
                        correct = newCorrect,
                        wrong = newWrong,
                        options = options,
                        previewIntervals = previews
                    )
                }
            }
        }
    }

    private fun computePreviews(card: StudyCard): Map<SrsRating, Int> =
        SrsRating.entries.associateWith { scheduler.previewIntervalDays(card.state, it) }
}
