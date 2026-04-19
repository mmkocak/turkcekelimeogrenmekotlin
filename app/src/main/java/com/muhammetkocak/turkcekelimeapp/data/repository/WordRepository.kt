package com.muhammetkocak.turkcekelimeapp.data.repository

import com.muhammetkocak.turkcekelimeapp.core.datetime.Clock
import com.muhammetkocak.turkcekelimeapp.data.local.AppDatabase
import com.muhammetkocak.turkcekelimeapp.data.local.dao.CardStateDao
import com.muhammetkocak.turkcekelimeapp.data.local.dao.CategoryDao
import com.muhammetkocak.turkcekelimeapp.data.local.dao.WordDao
import com.muhammetkocak.turkcekelimeapp.data.local.entity.CardStateEntity
import com.muhammetkocak.turkcekelimeapp.data.mapper.toDomain
import com.muhammetkocak.turkcekelimeapp.data.mapper.toEntity
import com.muhammetkocak.turkcekelimeapp.di.IoDispatcher
import com.muhammetkocak.turkcekelimeapp.domain.model.Category
import com.muhammetkocak.turkcekelimeapp.domain.model.LearningDirection
import com.muhammetkocak.turkcekelimeapp.domain.model.Word
import androidx.room.withTransaction
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

@Singleton
class WordRepository @Inject constructor(
    private val database: AppDatabase,
    private val wordDao: WordDao,
    private val categoryDao: CategoryDao,
    private val cardStateDao: CardStateDao,
    private val clock: Clock,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {

    fun observeCategories(): Flow<List<Category>> =
        categoryDao.observeAll().map { list -> list.map { it.toDomain() } }

    fun observeWords(categoryId: Long? = null): Flow<List<Word>> {
        val source = if (categoryId == null) wordDao.observeAll() else wordDao.observeByCategory(categoryId)
        return source.map { list -> list.map { it.toDomain() } }
    }

    fun searchWords(query: String): Flow<List<Word>> =
        wordDao.search(query.trim()).map { list -> list.map { it.toDomain() } }

    fun observeFavorites(): Flow<List<Word>> =
        wordDao.observeFavorites().map { list -> list.map { it.toDomain() } }

    fun observeUserCreated(): Flow<List<Word>> =
        wordDao.observeUserCreated().map { list -> list.map { it.toDomain() } }

    fun observeWord(id: Long): Flow<Word?> =
        wordDao.observeById(id).map { it?.toDomain() }

    suspend fun getWord(id: Long): Word? = withContext(ioDispatcher) {
        wordDao.getById(id)?.toDomain()
    }

    /**
     * Yeni kelime eklenirse iki yönde de [CardStateEntity] yaratılır.
     * Mevcut kelime güncellenirse kart state'ine dokunulmaz.
     */
    suspend fun upsertWord(word: Word): Long = withContext(ioDispatcher) {
        val now = clock.nowMillis()
        val entity = word.toEntity().let {
            if (it.createdAt == 0L) it.copy(createdAt = now) else it
        }
        var resultId = entity.id
        database.withTransaction {
            val id = wordDao.upsert(entity)
            resultId = if (id > 0L) id else entity.id
            if (entity.id == 0L && resultId > 0L) {
                cardStateDao.insertAll(
                    LearningDirection.entries.map { direction ->
                        CardStateEntity(
                            wordId = resultId,
                            direction = direction.raw,
                            dueAt = now
                        )
                    }
                )
            }
        }
        resultId
    }

    suspend fun setFavorite(id: Long, favorite: Boolean) = withContext(ioDispatcher) {
        wordDao.setFavorite(id, favorite)
    }

    suspend fun deleteWord(word: Word) = withContext(ioDispatcher) {
        wordDao.delete(word.toEntity())
    }

    suspend fun observeMasteredCount(categoryId: Long, direction: LearningDirection): Flow<Int> =
        cardStateDao.observeMasteredCountForCategory(categoryId, direction.raw)
}
