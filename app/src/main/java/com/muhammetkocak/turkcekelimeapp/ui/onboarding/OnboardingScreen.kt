package com.muhammetkocak.turkcekelimeapp.ui.onboarding

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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.muhammetkocak.turkcekelimeapp.domain.model.LearningDirection
import com.muhammetkocak.turkcekelimeapp.ui.component.PrimaryButton
import kotlinx.coroutines.launch

@Composable
fun OnboardingScreen(
    onFinished: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val pagerState = rememberPagerState(pageCount = { 3 })
    val scope = rememberCoroutineScope()
    var direction by remember { mutableStateOf(LearningDirection.ForeignToTurkish) }
    var dailyGoal by remember { mutableIntStateOf(20) }

    Column(Modifier.fillMaxSize().padding(24.dp)) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth().weight(1f)
        ) { page ->
            when (page) {
                0 -> WelcomePage()
                1 -> DirectionPage(selected = direction, onSelect = { direction = it })
                2 -> DailyGoalPage(selected = dailyGoal, onSelect = { dailyGoal = it })
            }
        }
        Row(
            Modifier.fillMaxWidth().padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            (0 until 3).forEach { i ->
                val isSel = pagerState.currentPage == i
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(if (isSel) 10.dp else 8.dp)
                        .clip(CircleShape)
                        .background(
                            if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
                        )
                )
            }
        }
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(
                onClick = { viewModel.finish(direction, dailyGoal, onFinished) },
                enabled = pagerState.currentPage < 2
            ) { Text("Atla") }
            if (pagerState.currentPage < 2) {
                OutlinedButton(
                    onClick = { scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) } },
                    shape = MaterialTheme.shapes.extraLarge,
                    modifier = Modifier.height(52.dp)
                ) { Text("İleri") }
            } else {
                PrimaryButton(
                    text = "Başla",
                    onClick = { viewModel.finish(direction, dailyGoal, onFinished) }
                )
            }
        }
    }
}

@Composable
private fun WelcomePage() {
    PageLayout(
        emoji = "🇹🇷",
        title = "Türkçe Kelime Öğrenme'ye hoş geldin",
        body = "Günde 5 dakika, akıllı tekrar (SRS) ile kelime dağarcığını sağlam kur. Offline çalışır, kendi kelimelerini de ekleyebilirsin."
    )
}

@Composable
private fun DirectionPage(selected: LearningDirection, onSelect: (LearningDirection) -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "🎯", fontSize = 72.sp)
        Text(
            text = "Hangi yönde çalışacaksın?",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            textAlign = TextAlign.Center
        )
        Text(
            text = "Kartın ön yüzünde hangi dilin olacağını seç. İstediğin zaman Ayarlar'dan değiştirebilirsin.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(8.dp))
        SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
            LearningDirection.entries.forEachIndexed { i, dir ->
                SegmentedButton(
                    selected = dir == selected,
                    onClick = { onSelect(dir) },
                    shape = SegmentedButtonDefaults.itemShape(index = i, count = LearningDirection.entries.size),
                    label = {
                        Text(
                            when (dir) {
                                LearningDirection.ForeignToTurkish -> "EN → TR"
                                LearningDirection.TurkishToForeign -> "TR → EN"
                            }
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun DailyGoalPage(selected: Int, onSelect: (Int) -> Unit) {
    val options = listOf(10, 20, 30, 50)
    Column(
        modifier = Modifier.fillMaxSize().padding(vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "📅", fontSize = 72.sp)
        Text(
            text = "Günlük hedefin?",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            textAlign = TextAlign.Center
        )
        Text(
            text = "Her gün kaç kart çalışmak istersin? Küçük başlamak büyük kazanır.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
            options.forEachIndexed { i, value ->
                SegmentedButton(
                    selected = value == selected,
                    onClick = { onSelect(value) },
                    shape = SegmentedButtonDefaults.itemShape(index = i, count = options.size),
                    label = { Text("$value") }
                )
            }
        }
    }
}

@Composable
private fun PageLayout(emoji: String, title: String, body: String) {
    Column(
        modifier = Modifier.fillMaxSize().padding(vertical = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = emoji, fontSize = 96.sp)
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            textAlign = TextAlign.Center
        )
        Text(
            text = body,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}
