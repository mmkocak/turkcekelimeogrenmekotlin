package com.muhammetkocak.turkcekelimeapp.data.local.seed

import android.content.Context
import androidx.room.withTransaction
import com.muhammetkocak.turkcekelimeapp.core.datetime.Clock
import com.muhammetkocak.turkcekelimeapp.core.ext.AppJson
import com.muhammetkocak.turkcekelimeapp.data.local.AppDatabase
import com.muhammetkocak.turkcekelimeapp.data.local.entity.CardStateEntity
import com.muhammetkocak.turkcekelimeapp.data.local.entity.CategoryEntity
import com.muhammetkocak.turkcekelimeapp.data.local.entity.WordEntity
import com.muhammetkocak.turkcekelimeapp.data.prefs.UserPreferencesDataStore
import com.muhammetkocak.turkcekelimeapp.di.IoDispatcher
import com.muhammetkocak.turkcekelimeapp.domain.model.LearningDirection
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

@Singleton
class SeedLoader @Inject constructor(
    @ApplicationContext private val context: Context,
    private val database: AppDatabase,
    private val preferences: UserPreferencesDataStore,
    private val clock: Clock,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {

    suspend fun seedIfNeeded() = withContext(ioDispatcher) {
        val prefs = preferences.preferences.first()
        if (prefs.firstRunCompleted) return@withContext
        if (database.wordDao().count() > 0) {
            preferences.markFirstRunCompleted()
            return@withContext
        }

        val payload = readSeedPayload()
        val now = clock.nowMillis()

        database.withTransaction {
            database.categoryDao().upsertAll(payload.categories.map { it.toEntity() })

            val insertedIds = database.wordDao().insertAll(
                payload.words.map { it.toEntity(createdAt = now) }
            )

            val cardStates = mutableListOf<CardStateEntity>()
            insertedIds.forEach { id ->
                if (id <= 0L) return@forEach
                LearningDirection.entries.forEach { direction ->
                    cardStates += CardStateEntity(
                        wordId = id,
                        direction = direction.raw,
                        dueAt = now
                    )
                }
            }
            database.cardStateDao().insertAll(cardStates)
        }

        preferences.markFirstRunCompleted()
    }

    private fun readSeedPayload(): SeedPayload =
        context.assets.open(SEED_FILE).bufferedReader().use { reader ->
            AppJson.decodeFromString(SeedPayload.serializer(), reader.readText())
        }

    private fun SeedCategory.toEntity(): CategoryEntity = CategoryEntity(
        id = id,
        nameTr = nameTr,
        nameEn = nameEn,
        emoji = emoji,
        colorHex = colorHex,
        orderIndex = orderIndex
    )

    private fun SeedWord.toEntity(createdAt: Long): WordEntity = WordEntity(
        foreignTerm = foreignTerm,
        turkishTerm = turkishTerm,
        partOfSpeech = partOfSpeech,
        exampleForeign = exampleForeign,
        exampleTurkish = exampleTurkish,
        ipa = ipa,
        categoryId = categoryId,
        isUserCreated = false,
        isFavorite = false,
        createdAt = createdAt
    )

    private companion object {
        const val SEED_FILE = "words.json"
    }
}
