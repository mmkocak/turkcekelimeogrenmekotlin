package com.muhammetkocak.turkcekelimeapp.ui.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.muhammetkocak.turkcekelimeapp.data.repository.WordRepository
import com.muhammetkocak.turkcekelimeapp.domain.model.Category
import com.muhammetkocak.turkcekelimeapp.domain.model.Word
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class LibraryFilter { All, Favorites, UserCreated, Mastered }

data class LibraryUiState(
    val categories: List<Category> = emptyList(),
    val words: List<Word> = emptyList(),
    val query: String = "",
    val selectedCategoryId: Long? = null,
    val filter: LibraryFilter = LibraryFilter.All,
    val loading: Boolean = true
)

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val wordRepository: WordRepository
) : ViewModel() {

    private val query = MutableStateFlow("")
    private val selectedCategory = MutableStateFlow<Long?>(null)
    private val filter = MutableStateFlow(LibraryFilter.All)

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val state: StateFlow<LibraryUiState> = combine(
        wordRepository.observeCategories(),
        query.debounce(180),
        selectedCategory,
        filter
    ) { categories, q, catId, f ->
        Quad(categories, q, catId, f)
    }.flatMapLatest { (categories, q, catId, f) ->
        val wordsFlow = when {
            q.isNotBlank() -> wordRepository.searchWords(q)
            f == LibraryFilter.Favorites -> wordRepository.observeFavorites()
            f == LibraryFilter.UserCreated -> wordRepository.observeUserCreated()
            catId != null -> wordRepository.observeWords(catId)
            else -> wordRepository.observeWords(null)
        }
        combine(wordsFlow, wordRepository.observeMasteredWordIds()) { ws, masteredIds ->
            val filtered = when (f) {
                LibraryFilter.Mastered -> {
                    val set = masteredIds.toHashSet()
                    ws.filter { it.id in set }
                }
                else -> ws
            }
            LibraryUiState(
                categories = categories,
                words = filtered,
                query = q,
                selectedCategoryId = catId,
                filter = f,
                loading = false
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), LibraryUiState())

    fun setQuery(value: String) { query.value = value }
    fun setCategory(id: Long?) { selectedCategory.value = id }
    fun setFilter(f: LibraryFilter) { filter.value = f }

    fun toggleFavorite(word: Word) {
        viewModelScope.launch {
            wordRepository.setFavorite(word.id, !word.isFavorite)
        }
    }

    fun delete(word: Word) {
        viewModelScope.launch { wordRepository.deleteWord(word) }
    }

    private data class Quad(
        val categories: List<Category>,
        val query: String,
        val categoryId: Long?,
        val filter: LibraryFilter
    )
}
