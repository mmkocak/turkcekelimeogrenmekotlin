package com.muhammetkocak.turkcekelimeapp.core.tts

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent

val LocalTtsManager: ProvidableCompositionLocal<TextToSpeechManager?> = compositionLocalOf { null }

@EntryPoint
@InstallIn(SingletonComponent::class)
interface TtsEntryPoint {
    fun ttsManager(): TextToSpeechManager
}

@Composable
fun rememberTtsManager(): TextToSpeechManager {
    val context = LocalContext.current
    return remember(context) {
        EntryPointAccessors.fromApplication(context.applicationContext, TtsEntryPoint::class.java).ttsManager()
    }
}
