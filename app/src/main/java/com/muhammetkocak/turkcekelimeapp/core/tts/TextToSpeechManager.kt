package com.muhammetkocak.turkcekelimeapp.core.tts

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import com.muhammetkocak.turkcekelimeapp.domain.model.LearningDirection
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Locale
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

@Singleton
class TextToSpeechManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var engine: TextToSpeech? = null
    private val ready = AtomicBoolean(false)

    private suspend fun ensureEngine(): TextToSpeech? {
        engine?.let { if (ready.get()) return it }
        return suspendCancellableCoroutine { cont ->
            val tts = TextToSpeech(context) { status ->
                if (status == TextToSpeech.SUCCESS) {
                    ready.set(true)
                    if (cont.isActive) cont.resume(engine)
                } else {
                    if (cont.isActive) cont.resume(null)
                }
            }
            engine = tts
            cont.invokeOnCancellation { /* keep engine alive */ }
        }
    }

    suspend fun isLanguageAvailable(direction: LearningDirection, side: Side): Boolean {
        val tts = ensureEngine() ?: return false
        val locale = localeFor(direction, side)
        val availability = tts.isLanguageAvailable(locale)
        return availability >= TextToSpeech.LANG_AVAILABLE
    }

    suspend fun speak(text: String, direction: LearningDirection, side: Side, rateScale: Float = 1f) {
        if (text.isBlank()) return
        val tts = ensureEngine() ?: return
        val locale = localeFor(direction, side)
        tts.language = locale
        tts.setSpeechRate(rateScale.coerceIn(0.5f, 1.5f))
        tts.setOnUtteranceProgressListener(NoopListener)
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, UTTERANCE_ID)
    }

    fun stop() {
        engine?.stop()
    }

    fun shutdown() {
        engine?.stop()
        engine?.shutdown()
        engine = null
        ready.set(false)
    }

    private fun localeFor(direction: LearningDirection, side: Side): Locale = when (side) {
        Side.Foreign -> Locale.ENGLISH
        Side.Turkish -> TurkishLocale
        Side.Prompt -> when (direction) {
            LearningDirection.ForeignToTurkish -> Locale.ENGLISH
            LearningDirection.TurkishToForeign -> TurkishLocale
        }
        Side.Answer -> when (direction) {
            LearningDirection.ForeignToTurkish -> TurkishLocale
            LearningDirection.TurkishToForeign -> Locale.ENGLISH
        }
    }

    enum class Side { Foreign, Turkish, Prompt, Answer }

    companion object {
        private const val UTTERANCE_ID = "tke-utterance"
        private val TurkishLocale = Locale("tr", "TR")
    }

    private object NoopListener : UtteranceProgressListener() {
        override fun onStart(utteranceId: String?) = Unit
        override fun onDone(utteranceId: String?) = Unit
        @Deprecated("Deprecated in Java")
        override fun onError(utteranceId: String?) = Unit
    }
}
