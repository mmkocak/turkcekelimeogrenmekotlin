package com.muhammetkocak.turkcekelimeapp.ui.study.listening

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.muhammetkocak.turkcekelimeapp.core.tts.TextToSpeechManager
import com.muhammetkocak.turkcekelimeapp.domain.model.SrsRating
import com.muhammetkocak.turkcekelimeapp.ui.study.SessionSummaryContent
import com.muhammetkocak.turkcekelimeapp.ui.study.session.StudySessionViewModel
import com.muhammetkocak.turkcekelimeapp.ui.study.session.StudyUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListeningScreen(
    onExit: () -> Unit,
    tts: TextToSpeechManager,
    viewModel: StudySessionViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dinleme") },
                navigationIcon = {
                    IconButton(onClick = onExit) {
                        Icon(Icons.Filled.Close, contentDescription = "Çıkış")
                    }
                }
            )
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            LinearProgressIndicator(progress = { state.progress }, modifier = Modifier.fillMaxWidth())
            when {
                state.loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Yükleniyor…") }
                state.finished -> SessionSummaryContent(summary = state.summary, onExit = onExit, onRestart = onExit)
                else -> ListeningContent(state = state, tts = tts, onSubmit = viewModel::submitRating)
            }
        }
    }
}

@Composable
private fun ListeningContent(
    state: StudyUiState,
    tts: TextToSpeechManager,
    onSubmit: (SrsRating, Boolean) -> Unit
) {
    val card = state.currentCard ?: return
    val options = state.options ?: return
    val haptic = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()
    var selected by remember(card.word.id) { mutableStateOf<Int?>(null) }
    var rate by remember { mutableFloatStateOf(1f) }

    LaunchedEffect(card.word.id) {
        tts.speak(card.word.prompt(card.direction), card.direction, TextToSpeechManager.Side.Prompt, rate)
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(160.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            IconButton(
                onClick = {
                    scope.launch {
                        tts.speak(card.word.prompt(card.direction), card.direction, TextToSpeechManager.Side.Prompt, rate)
                    }
                },
                modifier = Modifier.size(120.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.PlayArrow,
                    contentDescription = "Seslendir",
                    modifier = Modifier.size(72.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(selected = rate == 0.75f, onClick = { rate = 0.75f }, label = { Text("0.75x") })
            FilterChip(selected = rate == 1f, onClick = { rate = 1f }, label = { Text("1x") })
        }

        Text(
            text = "Doğru karşılığı seç",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        val correctIndex = options.correctIndex()
        options.options.forEachIndexed { index, word ->
            val label = word.answer(card.direction)
            val isSelected = selected == index
            val container = when {
                selected == null -> MaterialTheme.colorScheme.surface
                index == correctIndex -> MaterialTheme.colorScheme.primary.copy(alpha = 0.22f)
                isSelected -> MaterialTheme.colorScheme.error.copy(alpha = 0.22f)
                else -> MaterialTheme.colorScheme.surface
            }
            Card(
                onClick = {
                    if (selected != null) return@Card
                    selected = index
                    val correct = index == correctIndex
                    haptic.performHapticFeedback(
                        if (correct) HapticFeedbackType.LongPress else HapticFeedbackType.TextHandleMove
                    )
                    val rating = if (correct) SrsRating.Good else SrsRating.Again
                    scope.launch {
                        delay(900)
                        onSubmit(rating, correct)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(containerColor = container),
                elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 4.dp else 1.dp)
            ) {
                Text(text = label, modifier = Modifier.padding(18.dp), style = MaterialTheme.typography.titleMedium)
            }
        }

        Spacer(Modifier.height(8.dp))
    }
}
