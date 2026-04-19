package com.muhammetkocak.turkcekelimeapp.di

import android.content.Context
import androidx.room.Room
import com.muhammetkocak.turkcekelimeapp.data.local.AppDatabase
import com.muhammetkocak.turkcekelimeapp.data.local.dao.CardStateDao
import com.muhammetkocak.turkcekelimeapp.data.local.dao.CategoryDao
import com.muhammetkocak.turkcekelimeapp.data.local.dao.ReviewDao
import com.muhammetkocak.turkcekelimeapp.data.local.dao.SessionDao
import com.muhammetkocak.turkcekelimeapp.data.local.dao.WordDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, AppDatabase.NAME)
            .fallbackToDestructiveMigration()
            .build()

    @Provides fun provideCategoryDao(db: AppDatabase): CategoryDao = db.categoryDao()
    @Provides fun provideWordDao(db: AppDatabase): WordDao = db.wordDao()
    @Provides fun provideCardStateDao(db: AppDatabase): CardStateDao = db.cardStateDao()
    @Provides fun provideReviewDao(db: AppDatabase): ReviewDao = db.reviewDao()
    @Provides fun provideSessionDao(db: AppDatabase): SessionDao = db.sessionDao()
}
