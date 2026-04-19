package com.muhammetkocak.turkcekelimeapp.ui.study.typing

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.muhammetkocak.turkcekelimeapp.domain.model.SrsRating
import com.muhammetkocak.turkcekelimeapp.ui.component.PrimaryButton
import com.muhammetkocak.turkcekelimeapp.ui.study.SessionSummaryContent
import com.muhammetkocak.turkcekelimeapp.ui.study.session.StudySessionViewModel
import com.muhammetkocak.turkcekelimeapp.ui.study.session.StudyUiState
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TypingScreen(
    onExit: () -> Unit,
    viewModel: StudySessionViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Yazarak") },
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
                else -> TypingContent(state, viewModel::submitRating)
            }
        }
    }
}

@Composable
private fun TypingContent(state: StudyUiState, onSubmit: (SrsRating, Boolean) -> Unit) {
    val card = state.currentCard ?: return
    var answer by remember(card.word.id) { mutableStateOf("") }
    var attempts by remember(card.word.id) { mutableStateOf(0) }
    var hintShown by remember(card.word.id) { mutableStateOf(false) }
    var feedback by remember(card.word.id) { mutableStateOf<Boolean?>(null) }

    val expected = card.word.answer(card.direction)

    Column(
        modifier = Modifier.fillMaxSize().padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = "Karşılığını yaz",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.extraLarge,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
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
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        OutlinedTextField(
            value = answer,
            onValueChange = { answer = it; feedback = null },
            label = { Text("Cevabın") },
            singleLine = true,
            isError = feedback == false,
            modifier = Modifier.fillMaxWidth()
        )

        if (hintShown) {
            Text(
                text = "İpucu: ${expected.take(1)}… (${expected.length} harf)",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.secondary
            )
        }

        when (feedback) {
            true -> Text("Doğru!", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.titleMedium)
            false -> Text(
                text = "Doğrusu: $expected",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.titleMedium
            )
            null -> Unit
        }

        Spacer(Modifier.weight(1f))

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(
                onClick = { hintShown = true },
                enabled = !hintShown,
                modifier = Modifier.weight(1f).height(52.dp),
                shape = MaterialTheme.shapes.large
            ) { Text("İpucu") }
            OutlinedButton(
                onClick = { onSubmit(SrsRating.Again, false) },
                modifier = Modifier.weight(1f).height(52.dp),
                shape = MaterialTheme.shapes.large
            ) { Text("Atla") }
        }
        PrimaryButton(
            text = if (feedback == null) "Kontrol et" else "Devam",
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                if (feedback == null) {
                    val correct = normalize(answer) == normalize(expected)
                    feedback = correct
                    attempts += 1
                    if (correct) {
                        val rating = when {
                            hintShown -> SrsRating.Hard
                            attempts == 1 -> SrsRating.Good
                            else -> SrsRating.Hard
                        }
                        onSubmit(rating, true)
                    }
                } else if (feedback == false) {
                    onSubmit(SrsRating.Again, false)
                }
            }
        )
    }
}

private fun normalize(input: String): String =
    input.trim().lowercase(Locale("tr", "TR"))
