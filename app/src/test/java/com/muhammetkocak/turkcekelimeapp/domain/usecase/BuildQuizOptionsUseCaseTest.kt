package com.muhammetkocak.turkcekelimeapp.domain.usecase

import com.muhammetkocak.turkcekelimeapp.data.local.dao.WordDao
import com.muhammetkocak.turkcekelimeapp.data.local.entity.WordEntity
import com.muhammetkocak.turkcekelimeapp.domain.model.LearningDirection
import com.muhammetkocak.turkcekelimeapp.domain.model.Word
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class BuildQuizOptionsUseCaseTest {

    private val dispatcher = UnconfinedTestDispatcher()

    private fun word(id: Long, category: Long, foreign: String, turkish: String) = Word(
        id = id,
        foreignTerm = foreign,
        turkishTerm = turkish,
        categoryId = category,
        createdAt = 0L
    )

    private fun entity(id: Long, category: Long, foreign: String, turkish: String) = WordEntity(
        id = id,
        foreignTerm = foreign,
        turkishTerm = turkish,
        categoryId = category,
        createdAt = 0L
    )

    @Test
    fun `picks distractors from same category when enough exist`() = runTest {
        val correct = word(1L, 10L, "apple", "elma")
        val dao = FakeWordDao(
            sameCategory = listOf(
                entity(2L, 10L, "pear", "armut"),
                entity(3L, 10L, "plum", "erik"),
                entity(4L, 10L, "grape", "üzüm")
            ),
            global = emptyList()
        )
        val useCase = BuildQuizOptionsUseCase(dao, dispatcher)

        val options = useCase(correct, LearningDirection.ForeignToTurkish)

        assertEquals(4, options.options.size)
        assertTrue("correct word must be present", options.options.any { it.id == correct.id })
        assertTrue("all distractors from same category", options.options.all { it.categoryId == 10L })
        assertTrue("no duplicates", options.options.map { it.id }.distinct().size == options.options.size)
    }

    @Test
    fun `falls back to global pool when category lacks distractors`() = runTest {
        val correct = word(1L, 10L, "apple", "elma")
        val dao = FakeWordDao(
            sameCategory = listOf(entity(2L, 10L, "pear", "armut")),
            global = listOf(
                entity(3L, 99L, "car", "araba"),
                entity(4L, 99L, "house", "ev"),
                entity(5L, 99L, "tree", "ağaç")
            )
        )
        val useCase = BuildQuizOptionsUseCase(dao, dispatcher)

        val options = useCase(correct, LearningDirection.ForeignToTurkish)

        assertEquals(4, options.options.size)
        assertTrue(options.options.any { it.id == correct.id })
        assertEquals(0, options.options.count { it.id == correct.id }.let { 1 - it })
    }

    @Test
    fun `correctIndex returns position of the correct option`() = runTest {
        val correct = word(7L, 10L, "run", "koşmak")
        val dao = FakeWordDao(
            sameCategory = listOf(
                entity(1L, 10L, "walk", "yürümek"),
                entity(2L, 10L, "jump", "zıplamak"),
                entity(3L, 10L, "swim", "yüzmek")
            )
        )
        val useCase = BuildQuizOptionsUseCase(dao, dispatcher)

        val options = useCase(correct, LearningDirection.TurkishToForeign)
        val idx = options.correctIndex()

        assertTrue(idx in 0..3)
        assertEquals(correct.id, options.options[idx].id)
    }
}

private class FakeWordDao(
    private val sameCategory: List<WordEntity> = emptyList(),
    private val global: List<WordEntity> = emptyList()
) : WordDao {
    override suspend fun randomFromCategory(categoryId: Long, excludeId: Long, limit: Int): List<WordEntity> =
        sameCategory.filter { it.categoryId == categoryId && it.id != excludeId }.shuffled().take(limit)

    override suspend fun randomGlobal(excludeId: Long, limit: Int): List<WordEntity> =
        global.filter { it.id != excludeId }.shuffled().take(limit)

    override fun observeAll(): Flow<List<WordEntity>> = unused()
    override fun observeByCategory(categoryId: Long): Flow<List<WordEntity>> = unused()
    override fun search(query: String): Flow<List<WordEntity>> = unused()
    override fun observeFavorites(): Flow<List<WordEntity>> = unused()
    override fun observeUserCreated(): Flow<List<WordEntity>> = unused()
    override suspend fun getById(id: Long): WordEntity? = unused()
    override fun observeById(id: Long): Flow<WordEntity?> = unused()
    override suspend fun count(): Int = unused()
    override suspend fun insert(word: WordEntity): Long = unused()
    override suspend fun insertAll(words: List<WordEntity>): List<Long> = unused()
    override suspend fun upsert(word: WordEntity): Long = unused()
    override suspend fun update(word: WordEntity): Unit = unused()
    override suspend fun delete(word: WordEntity): Unit = unused()
    override suspend fun setFavorite(id: Long, favorite: Boolean): Unit = unused()

    private fun <T> unused(): T = throw NotImplementedError("Not used in this test")
}
