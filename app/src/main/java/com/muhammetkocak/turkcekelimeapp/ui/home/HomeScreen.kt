package com.muhammetkocak.turkcekelimeapp.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.muhammetkocak.turkcekelimeapp.domain.model.Category
import com.muhammetkocak.turkcekelimeapp.domain.model.LearningDirection
import com.muhammetkocak.turkcekelimeapp.domain.model.StudyMode
import com.muhammetkocak.turkcekelimeapp.ui.component.IconChipCard
import com.muhammetkocak.turkcekelimeapp.ui.component.ProgressRing
import com.muhammetkocak.turkcekelimeapp.ui.component.SectionHeader
import com.muhammetkocak.turkcekelimeapp.ui.component.StreakBadge

@Composable
fun HomeScreen(
    onOpenLibrary: (Long?) -> Unit,
    onOpenAddWord: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenStats: () -> Unit,
    onStartStudy: (StudyMode, LearningDirection, Long?) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onOpenAddWord,
                icon = { Icon(Icons.Filled.Add, contentDescription = null) },
                text = { Text("Kelime Ekle") }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                HomeTopBar(
                    streak = state.summary?.currentStreak ?: 0,
                    onSettings = onOpenSettings,
                    onStats = onOpenStats,
                    onLibrary = { onOpenLibrary(null) }
                )
            }

            item {
                TodaySummaryCard(
                    dueCount = state.summary?.dueCount ?: 0,
                    todayReviewCount = state.summary?.todayReviewCount ?: 0,
                    dailyGoal = state.summary?.dailyGoal ?: 20,
                    progress = state.summary?.goalProgress ?: 0f,
                    direction = state.preferences.primaryDirection,
                    onStart = {
                        onStartStudy(StudyMode.Flashcard, state.preferences.primaryDirection, null)
                    }
                )
            }

            item { SectionHeader(title = "Çalışma Modu") }
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(StudyMode.entries.toList()) { mode ->
                        IconChipCard(
                            emoji = mode.emoji,
                            title = mode.labelTr,
                            subtitle = when (mode) {
                                StudyMode.Flashcard -> "Kart çevir, SRS ile tekrar et"
                                StudyMode.Quiz -> "Doğru anlamı 4 seçenekten seç"
                                StudyMode.Typing -> "Anlamı yazarak test et"
                                StudyMode.Listening -> "Dinle, sonra seç"
                            },
                            onClick = { onStartStudy(mode, state.preferences.primaryDirection, null) },
                            modifier = Modifier.width(260.dp)
                        )
                    }
                }
            }

            item { SectionHeader(title = "Kategoriler") }
            items(state.categories.chunked(2)) { pair ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    pair.forEach { category ->
                        CategoryCard(
                            category = category,
                            modifier = Modifier.weight(1f),
                            onClick = { onOpenLibrary(category.id) }
                        )
                    }
                    if (pair.size == 1) Spacer(Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun HomeTopBar(
    streak: Int,
    onSettings: () -> Unit,
    onStats: () -> Unit,
    onLibrary: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        StreakBadge(streak = streak)
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            IconButton(onClick = onLibrary) {
                Icon(Icons.Filled.LibraryBooks, contentDescription = "Kütüphane")
            }
            IconButton(onClick = onStats) {
                Icon(Icons.Filled.BarChart, contentDescription = "İstatistikler")
            }
            IconButton(onClick = onSettings) {
                Icon(Icons.Filled.Settings, contentDescription = "Ayarlar")
            }
        }
    }
}

@Composable
private fun TodaySummaryCard(
    dueCount: Int,
    todayReviewCount: Int,
    dailyGoal: Int,
    progress: Float,
    direction: LearningDirection,
    onStart: () -> Unit
) {
    Card(
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            ProgressRing(
                progress = progress,
                modifier = Modifier.size(96.dp),
                progressColor = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$todayReviewCount",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "/$dailyGoal",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                }
            }
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Bugünkü Çalışma",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "$dueCount kart hazır · ${direction.labelShort()}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
                )
                Button(
                    onClick = onStart,
                    shape = MaterialTheme.shapes.extraLarge,
                    modifier = Modifier.semantics { contentDescription = "Çalışmaya başla" }
                ) {
                    Text(text = "Çalışmaya Başla")
                }
            }
        }
    }
}

@Composable
private fun CategoryCard(category: Category, modifier: Modifier, onClick: () -> Unit) {
    val accent = runCatching { Color(android.graphics.Color.parseColor(category.colorHex)) }
        .getOrDefault(MaterialTheme.colorScheme.primary)
    Card(
        onClick = onClick,
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(accent.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center
            ) { Text(text = category.emoji, fontSize = 22.sp) }
            Text(
                text = category.nameTr,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                maxLines = 1
            )
            Text(
                text = category.nameEn,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
        }
    }
}

private fun LearningDirection.labelShort(): String = when (this) {
    LearningDirection.ForeignToTurkish -> "EN → TR"
    LearningDirection.TurkishToForeign -> "TR → EN"
}
