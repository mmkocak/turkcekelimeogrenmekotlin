package com.muhammetkocak.turkcekelimeapp

import android.app.Application
import com.muhammetkocak.turkcekelimeapp.data.local.seed.SeedLoader
import com.muhammetkocak.turkcekelimeapp.di.ApplicationScope
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@HiltAndroidApp
class TurkceKelimeApp : Application() {

    @Inject lateinit var seedLoader: SeedLoader

    @Inject @ApplicationScope lateinit var applicationScope: CoroutineScope

    override fun onCreate() {
        super.onCreate()
        applicationScope.launch { seedLoader.seedIfNeeded() }
    }
}
