package com.muhammetkocak.turkcekelimeapp.domain.usecase

import com.muhammetkocak.turkcekelimeapp.data.local.dao.WordDao
import com.muhammetkocak.turkcekelimeapp.data.mapper.toDomain
import com.muhammetkocak.turkcekelimeapp.di.IoDispatcher
import com.muhammetkocak.turkcekelimeapp.domain.model.LearningDirection
import com.muhammetkocak.turkcekelimeapp.domain.model.Word
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

/**
 * Quiz / dinleme modları için 4 seçenekli soru hazırlar.
 * Distractor'lar önce aynı kategoriden seçilir; yetmezse global havuzdan tamamlanır.
 * Sonuç shuffle'lanmış; doğru cevap listede tek bir yerde bulunur.
 */
class BuildQuizOptionsUseCase @Inject constructor(
    private val wordDao: WordDao,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(
        correct: Word,
        direction: LearningDirection,
        optionCount: Int = DEFAULT_OPTIONS
    ): QuizOptions = withContext(ioDispatcher) {
        val needed = optionCount - 1
        val pool = mutableListOf<Word>()
        pool += wordDao.randomFromCategory(correct.categoryId, correct.id, needed).map { it.toDomain() }
        if (pool.size < needed) {
            val existingIds = (pool.map { it.id } + correct.id).toSet()
            val fill = wordDao.randomGlobal(correct.id, needed * 2).map { it.toDomain() }
                .filter { it.id !in existingIds }
            pool += fill.take(needed - pool.size)
        }
        val shuffled = (pool + correct).distinctBy { it.id }.shuffled()
        QuizOptions(
            correct = correct,
            direction = direction,
            options = shuffled
        )
    }

    private companion object {
        const val DEFAULT_OPTIONS = 4
    }
}

data class QuizOptions(
    val correct: Word,
    val direction: LearningDirection,
    val options: List<Word>
) {
    fun correctIndex(): Int = options.indexOfFirst { it.id == correct.id }
}
