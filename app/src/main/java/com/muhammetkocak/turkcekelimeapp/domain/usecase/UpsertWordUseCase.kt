package com.muhammetkocak.turkcekelimeapp.domain.usecase

import com.muhammetkocak.turkcekelimeapp.core.datetime.Clock
import com.muhammetkocak.turkcekelimeapp.data.repository.WordRepository
import com.muhammetkocak.turkcekelimeapp.domain.model.Word
import javax.inject.Inject

sealed class UpsertWordResult {
    data class Success(val wordId: Long) : UpsertWordResult()
    data object MissingForeignTerm : UpsertWordResult()
    data object MissingTurkishTerm : UpsertWordResult()
    data object MissingCategory : UpsertWordResult()
}

/**
 * Kullanıcı yeni kelime eklerken (veya düzenlerken) çağrılır.
 * Doğrulama + normalize + repo delegasyonu.
 */
class UpsertWordUseCase @Inject constructor(
    private val wordRepository: WordRepository,
    private val clock: Clock
) {
    suspend operator fun invoke(draft: Word): UpsertWordResult {
        val foreign = draft.foreignTerm.trim()
        val turkish = draft.turkishTerm.trim()
        if (foreign.isEmpty()) return UpsertWordResult.MissingForeignTerm
        if (turkish.isEmpty()) return UpsertWordResult.MissingTurkishTerm
        if (draft.categoryId <= 0L) return UpsertWordResult.MissingCategory

        val toSave = draft.copy(
            foreignTerm = foreign,
            turkishTerm = turkish,
            isUserCreated = draft.id == 0L || draft.isUserCreated,
            createdAt = if (draft.createdAt == 0L) clock.nowMillis() else draft.createdAt
        )
        val id = wordRepository.upsertWord(toSave)
        return UpsertWordResult.Success(id)
    }
}
