package com.muhammetkocak.turkcekelimeapp.ui.study.flashcard

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.muhammetkocak.turkcekelimeapp.core.tts.TextToSpeechManager
import com.muhammetkocak.turkcekelimeapp.domain.model.SrsRating
import com.muhammetkocak.turkcekelimeapp.domain.model.StudyCard
import com.muhammetkocak.turkcekelimeapp.ui.study.session.StudySessionViewModel
import com.muhammetkocak.turkcekelimeapp.ui.study.session.StudyUiState
import com.muhammetkocak.turkcekelimeapp.ui.study.SessionSummaryContent
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlashcardScreen(
    onExit: () -> Unit,
    tts: TextToSpeechManager,
    viewModel: StudySessionViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Flashcard") },
                navigationIcon = {
                    IconButton(onClick = onExit) {
                        Icon(Icons.Filled.Close, contentDescription = "Çıkış")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            LinearProgressIndicator(
                progress = { state.progress },
                modifier = Modifier.fillMaxWidth()
            )
            when {
                state.loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Yükleniyor…") }
                state.finished -> SessionSummaryContent(
                    summary = state.summary,
                    onExit = onExit,
                    onRestart = onExit
                )
                else -> FlashcardContent(
                    state = state,
                    tts = tts,
                    onRate = { rating -> viewModel.submitRating(rating, wasCorrect = rating != SrsRating.Again) }
                )
            }
        }
    }
}

@Composable
private fun FlashcardContent(
    state: StudyUiState,
    tts: TextToSpeechManager,
    onRate: (SrsRating) -> Unit
) {
    val card = state.currentCard ?: return
    var flipped by remember(card.word.id) { mutableStateOf(false) }
    val rotation by animateFloatAsState(
        targetValue = if (flipped) 180f else 0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "flip"
    )
    val haptic = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "${state.currentIndex + 1} / ${state.queue.size}",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.4f)
                .graphicsLayer {
                    rotationY = rotation
                    cameraDistance = 12f * density
                }
                .clickable {
                    flipped = !flipped
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                },
            shape = MaterialTheme.shapes.extraLarge,
            colors = CardDefaults.cardColors(
                containerColor = if (rotation <= 90f) MaterialTheme.colorScheme.primaryContainer
                else MaterialTheme.colorScheme.secondaryContainer
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Box(Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
                if (rotation <= 90f) {
                    FlashcardFace(
                        title = card.word.prompt(card.direction),
                        subtitle = card.word.promptExample(card.direction),
                        ipa = card.word.ipa,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        onSpeak = {
                            scope.launch { tts.speak(card.word.prompt(card.direction), card.direction, TextToSpeechManager.Side.Prompt) }
                        }
                    )
                } else {
                    Box(Modifier.graphicsLayer { rotationY = 180f }) {
                        FlashcardFace(
                            title = card.word.answer(card.direction),
                            subtitle = card.word.answerExample(card.direction),
                            ipa = null,
                            tint = MaterialTheme.colorScheme.onSecondaryContainer,
                            onSpeak = {
                                scope.launch { tts.speak(card.word.answer(card.direction), card.direction, TextToSpeechManager.Side.Answer) }
                            }
                        )
                    }
                }
            }
        }

        Text(
            text = if (flipped) "Zorluk değerlendir" else "Karta dokun → çevir",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(4.dp))

        RatingRow(state = state, onRate = onRate, enabled = flipped)
    }
}

@Composable
private fun FlashcardFace(
    title: String,
    subtitle: String?,
    ipa: String?,
    tint: Color,
    onSpeak: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconButton(onClick = onSpeak, modifier = Modifier.align(Alignment.End)) {
            Icon(Icons.Filled.VolumeUp, contentDescription = "Seslendir", tint = tint)
        }
        Spacer(Modifier.height(4.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
            color = tint,
            textAlign = TextAlign.Center
        )
        if (!ipa.isNullOrBlank()) {
            Spacer(Modifier.height(6.dp))
            Text(
                text = "/$ipa/",
                style = MaterialTheme.typography.bodyMedium,
                color = tint.copy(alpha = 0.7f)
            )
        }
        if (!subtitle.isNullOrBlank()) {
            Spacer(Modifier.height(12.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyLarge,
                color = tint.copy(alpha = 0.85f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun RatingRow(state: StudyUiState, onRate: (SrsRating) -> Unit, enabled: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SrsRating.entries.forEach { rating ->
            val interval = state.previewIntervals[rating] ?: 0
            val (bg, fg) = when (rating) {
                SrsRating.Again -> MaterialTheme.colorScheme.errorContainer to MaterialTheme.colorScheme.onErrorContainer
                SrsRating.Hard -> MaterialTheme.colorScheme.tertiaryContainer to MaterialTheme.colorScheme.onTertiaryContainer
                SrsRating.Good -> MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.onPrimaryContainer
                SrsRating.Easy -> MaterialTheme.colorScheme.secondaryContainer to MaterialTheme.colorScheme.onSecondaryContainer
            }
            Button(
                onClick = { onRate(rating) },
                enabled = enabled,
                modifier = Modifier.weight(1f).height(64.dp),
                shape = MaterialTheme.shapes.large,
                colors = ButtonDefaults.buttonColors(containerColor = bg, contentColor = fg)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(rating.labelTr, style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold))
                    Text(
                        text = formatInterval(interval),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }
}

private fun formatInterval(days: Int): String = when {
    days <= 0 -> "<1g"
    days == 1 -> "1g"
    days < 30 -> "${days}g"
    days < 365 -> "${days / 30}ay"
    else -> "${days / 365}y"
}
