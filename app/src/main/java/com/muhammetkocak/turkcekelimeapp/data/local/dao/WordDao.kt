package com.muhammetkocak.turkcekelimeapp.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.muhammetkocak.turkcekelimeapp.data.local.entity.WordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WordDao {

    @Query("SELECT * FROM words ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<WordEntity>>

    @Query("SELECT * FROM words WHERE categoryId = :categoryId ORDER BY foreignTerm ASC")
    fun observeByCategory(categoryId: Long): Flow<List<WordEntity>>

    @Query("""
        SELECT * FROM words
        WHERE foreignTerm LIKE '%' || :query || '%'
           OR turkishTerm LIKE '%' || :query || '%'
        ORDER BY foreignTerm ASC
    """)
    fun search(query: String): Flow<List<WordEntity>>

    @Query("SELECT * FROM words WHERE isFavorite = 1 ORDER BY foreignTerm ASC")
    fun observeFavorites(): Flow<List<WordEntity>>

    @Query("SELECT * FROM words WHERE isUserCreated = 1 ORDER BY createdAt DESC")
    fun observeUserCreated(): Flow<List<WordEntity>>

    @Query("SELECT * FROM words WHERE id = :id")
    suspend fun getById(id: Long): WordEntity?

    @Query("SELECT * FROM words WHERE id = :id")
    fun observeById(id: Long): Flow<WordEntity?>

    @Query("SELECT * FROM words WHERE categoryId = :categoryId AND id != :excludeId ORDER BY RANDOM() LIMIT :limit")
    suspend fun randomFromCategory(categoryId: Long, excludeId: Long, limit: Int): List<WordEntity>

    @Query("SELECT * FROM words WHERE id != :excludeId ORDER BY RANDOM() LIMIT :limit")
    suspend fun randomGlobal(excludeId: Long, limit: Int): List<WordEntity>

    @Query("SELECT COUNT(*) FROM words")
    suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(word: WordEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(words: List<WordEntity>): List<Long>

    @Upsert
    suspend fun upsert(word: WordEntity): Long

    @Update
    suspend fun update(word: WordEntity)

    @Delete
    suspend fun delete(word: WordEntity)

    @Query("UPDATE words SET isFavorite = :favorite WHERE id = :id")
    suspend fun setFavorite(id: Long, favorite: Boolean)
}
