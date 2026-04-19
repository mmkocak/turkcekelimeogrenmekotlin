package com.muhammetkocak.turkcekelimeapp.ui.addword

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.muhammetkocak.turkcekelimeapp.data.repository.WordRepository
import com.muhammetkocak.turkcekelimeapp.domain.model.Category
import com.muhammetkocak.turkcekelimeapp.domain.model.Word
import com.muhammetkocak.turkcekelimeapp.domain.usecase.UpsertWordResult
import com.muhammetkocak.turkcekelimeapp.domain.usecase.UpsertWordUseCase
import com.muhammetkocak.turkcekelimeapp.navigation.Screen
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AddEditWordUiState(
    val wordId: Long = 0L,
    val foreignTerm: String = "",
    val turkishTerm: String = "",
    val partOfSpeech: String? = null,
    val exampleForeign: String = "",
    val exampleTurkish: String = "",
    val ipa: String = "",
    val categoryId: Long? = null,
    val categories: List<Category> = emptyList(),
    val isEditing: Boolean = false,
    val saving: Boolean = false,
    val foreignError: Boolean = false,
    val turkishError: Boolean = false,
    val categoryError: Boolean = false
)

sealed interface AddEditWordEffect {
    data object Saved : AddEditWordEffect
    data class Error(val message: String) : AddEditWordEffect
}

val PartsOfSpeech: List<Pair<String, String>> = listOf(
    "noun" to "isim",
    "verb" to "fiil",
    "adjective" to "sıfat",
    "adverb" to "zarf",
    "phrase" to "öbek"
)

@HiltViewModel
class AddEditWordViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val wordRepository: WordRepository,
    private val upsertWord: UpsertWordUseCase
) : ViewModel() {

    private val route: Screen.AddEditWord = savedStateHandle.toRoute()

    private val _state = MutableStateFlow(AddEditWordUiState())
    val state: StateFlow<AddEditWordUiState> = _state.asStateFlow()

    private val _effects = Channel<AddEditWordEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    init {
        viewModelScope.launch {
            val categories = wordRepository.observeCategories()
            categories.collect { list ->
                _state.update { it.copy(categories = list, categoryId = it.categoryId ?: list.firstOrNull()?.id) }
            }
        }
        route.wordId?.takeIf { it > 0L }?.let { id ->
            viewModelScope.launch {
                val existing = wordRepository.getWord(id)
                if (existing != null) {
                    _state.update {
                        it.copy(
                            wordId = existing.id,
                            foreignTerm = existing.foreignTerm,
                            turkishTerm = existing.turkishTerm,
                            partOfSpeech = existing.partOfSpeech,
                            exampleForeign = existing.exampleForeign.orEmpty(),
                            exampleTurkish = existing.exampleTurkish.orEmpty(),
                            ipa = existing.ipa.orEmpty(),
                            categoryId = existing.categoryId,
                            isEditing = true
                        )
                    }
                }
            }
        }
    }

    fun setForeignTerm(value: String) = _state.update { it.copy(foreignTerm = value, foreignError = false) }
    fun setTurkishTerm(value: String) = _state.update { it.copy(turkishTerm = value, turkishError = false) }
    fun setPartOfSpeech(value: String?) = _state.update { it.copy(partOfSpeech = value) }
    fun setExampleForeign(value: String) = _state.update { it.copy(exampleForeign = value) }
    fun setExampleTurkish(value: String) = _state.update { it.copy(exampleTurkish = value) }
    fun setIpa(value: String) = _state.update { it.copy(ipa = value) }
    fun setCategory(id: Long) = _state.update { it.copy(categoryId = id, categoryError = false) }

    fun save() {
        val snapshot = _state.value
        if (snapshot.saving) return
        _state.update { it.copy(saving = true) }
        viewModelScope.launch {
            val draft = Word(
                id = snapshot.wordId,
                foreignTerm = snapshot.foreignTerm,
                turkishTerm = snapshot.turkishTerm,
                partOfSpeech = snapshot.partOfSpeech?.takeIf { it.isNotBlank() },
                exampleForeign = snapshot.exampleForeign.ifBlank { null },
                exampleTurkish = snapshot.exampleTurkish.ifBlank { null },
                ipa = snapshot.ipa.ifBlank { null },
                categoryId = snapshot.categoryId ?: 0L,
                isUserCreated = true,
                isFavorite = false,
                createdAt = 0L
            )
            when (val result = upsertWord(draft)) {
                is UpsertWordResult.Success -> {
                    _state.update { it.copy(saving = false) }
                    _effects.send(AddEditWordEffect.Saved)
                }
                UpsertWordResult.MissingForeignTerm -> {
                    _state.update { it.copy(saving = false, foreignError = true) }
                    _effects.send(AddEditWordEffect.Error("Yabancı kelime gerekli"))
                }
                UpsertWordResult.MissingTurkishTerm -> {
                    _state.update { it.copy(saving = false, turkishError = true) }
                    _effects.send(AddEditWordEffect.Error("Türkçe karşılık gerekli"))
                }
                UpsertWordResult.MissingCategory -> {
                    _state.update { it.copy(saving = false, categoryError = true) }
                    _effects.send(AddEditWordEffect.Error("Kategori seç"))
                }
            }
        }
    }
}
