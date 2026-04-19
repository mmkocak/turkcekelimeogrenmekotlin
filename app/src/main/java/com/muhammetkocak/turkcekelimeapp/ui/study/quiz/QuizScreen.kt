package com.muhammetkocak.turkcekelimeapp.ui.study.quiz

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.muhammetkocak.turkcekelimeapp.domain.model.SrsRating
import com.muhammetkocak.turkcekelimeapp.ui.study.SessionSummaryContent
import com.muhammetkocak.turkcekelimeapp.ui.study.session.StudySessionViewModel
import com.muhammetkocak.turkcekelimeapp.ui.study.session.StudyUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    onExit: () -> Unit,
    viewModel: StudySessionViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Çoktan Seçmeli") },
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
                else -> QuizContent(state, viewModel::submitRating)
            }
        }
    }
}

@Composable
private fun QuizContent(state: StudyUiState, onSubmit: (SrsRating, Boolean) -> Unit) {
    val card = state.currentCard ?: return
    val options = state.options ?: return
    val haptic = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()
    var selected by remember(card.word.id) { mutableStateOf<Int?>(null) }
    var startedAt by remember(card.word.id) { mutableLongStateOf(System.currentTimeMillis()) }
    LaunchedEffect(card.word.id) { startedAt = System.currentTimeMillis() }

    Column(
        modifier = Modifier.fillMaxSize().padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Anlamı nedir?",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.extraLarge,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(Modifier.fillMaxWidth().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = card.word.prompt(card.direction),
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                card.word.promptExample(card.direction)?.let {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

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
                    val elapsedMs = System.currentTimeMillis() - startedAt
                    val rating = when {
                        !correct -> SrsRating.Again
                        elapsedMs > 4000 -> SrsRating.Hard
                        else -> SrsRating.Good
                    }
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
                Text(
                    text = label,
                    modifier = Modifier.fillMaxWidth().padding(18.dp),
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}
