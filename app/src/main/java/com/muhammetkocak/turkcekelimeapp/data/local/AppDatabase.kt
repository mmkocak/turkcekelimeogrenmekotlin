package com.muhammetkocak.turkcekelimeapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.muhammetkocak.turkcekelimeapp.data.local.dao.CardStateDao
import com.muhammetkocak.turkcekelimeapp.data.local.dao.CategoryDao
import com.muhammetkocak.turkcekelimeapp.data.local.dao.ReviewDao
import com.muhammetkocak.turkcekelimeapp.data.local.dao.SessionDao
import com.muhammetkocak.turkcekelimeapp.data.local.dao.WordDao
import com.muhammetkocak.turkcekelimeapp.data.local.entity.CardStateEntity
import com.muhammetkocak.turkcekelimeapp.data.local.entity.CategoryEntity
import com.muhammetkocak.turkcekelimeapp.data.local.entity.ReviewEntity
import com.muhammetkocak.turkcekelimeapp.data.local.entity.SessionEntity
import com.muhammetkocak.turkcekelimeapp.data.local.entity.WordEntity

@Database(
    entities = [
        CategoryEntity::class,
        WordEntity::class,
        CardStateEntity::class,
        ReviewEntity::class,
        SessionEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun wordDao(): WordDao
    abstract fun cardStateDao(): CardStateDao
    abstract fun reviewDao(): ReviewDao
    abstract fun sessionDao(): SessionDao

    companion object {
        const val NAME = "turkce_kelime.db"
    }
}
