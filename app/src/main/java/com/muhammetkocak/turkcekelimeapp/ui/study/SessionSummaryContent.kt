package com.muhammetkocak.turkcekelimeapp.ui.study

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.muhammetkocak.turkcekelimeapp.ui.component.PrimaryButton
import com.muhammetkocak.turkcekelimeapp.ui.study.session.SessionSummary

@Composable
fun SessionSummaryContent(
    summary: SessionSummary?,
    onExit: () -> Unit,
    onRestart: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(20.dp))
        Text(text = "🎉", fontSize = 64.sp)
        Text(
            text = "Bugünlük harikaydın!",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            textAlign = TextAlign.Center
        )
        if (summary != null) {
            Text(
                text = "${summary.mode.labelTr} · ${summary.totalReviewed} kart",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatCell(label = "Doğru", value = "${summary.correct}", color = MaterialTheme.colorScheme.primary)
                    StatCell(label = "Yanlış", value = "${summary.wrong}", color = MaterialTheme.colorScheme.error)
                    val accuracy = if (summary.totalReviewed > 0)
                        (summary.correct * 100f / summary.totalReviewed).toInt() else 0
                    StatCell(label = "Başarı", value = "%$accuracy", color = MaterialTheme.colorScheme.tertiary)
                }
            }
        } else {
            Text(
                text = "Bugün için bitmiş kartın yok. Yarın tekrar dene ya da yeni kelimeler ekle.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(Modifier.height(12.dp))
        PrimaryButton(text = "Ana Sayfa", onClick = onExit, modifier = Modifier.fillMaxWidth())
        OutlinedButton(
            onClick = onRestart,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = MaterialTheme.shapes.extraLarge
        ) { Text("Kapat") }
    }
}

@Composable
private fun StatCell(label: String, value: String, color: androidx.compose.ui.graphics.Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
