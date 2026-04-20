package com.muhammetkocak.turkcekelimeapp.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.LibraryBooks
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
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
import com.muhammetkocak.turkcekelimeapp.ui.component.SectionHeader
import com.muhammetkocak.turkcekelimeapp.ui.component.StreakBadge
import com.muhammetkocak.turkcekelimeapp.ui.theme.HeroGradientEnd
import com.muhammetkocak.turkcekelimeapp.ui.theme.HeroGradientEndDark
import com.muhammetkocak.turkcekelimeapp.ui.theme.HeroGradientStart
import com.muhammetkocak.turkcekelimeapp.ui.theme.HeroGradientStartDark

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
                text = { Text("Kelime Ekle", fontWeight = FontWeight.SemiBold) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                elevation = androidx.compose.material3.FloatingActionButtonDefaults.elevation(4.dp)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
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
                HeroCard(
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

            item { SectionHeader(title = "Çalışma modları") }
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(horizontal = 0.dp)
                ) {
                    items(StudyMode.entries.toList()) { mode ->
                        ModeCard(
                            mode = mode,
                            onClick = { onStartStudy(mode, state.preferences.primaryDirection, null) }
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

            item { Spacer(Modifier.height(72.dp)) } // FAB clearance
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
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = "Merhaba 👋",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = "Bugün öğrenelim",
                style = MaterialTheme.typography.headlineMedium
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            StreakBadge(streak = streak)
            TopIcon(Icons.AutoMirrored.Filled.LibraryBooks, "Kütüphane", onLibrary)
            TopIcon(Icons.Filled.BarChart, "İstatistikler", onStats)
            TopIcon(Icons.Filled.Settings, "Ayarlar", onSettings)
        }
    }
}

@Composable
private fun TopIcon(icon: androidx.compose.ui.graphics.vector.ImageVector, description: String, onClick: () -> Unit) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Icon(icon, contentDescription = description, tint = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun HeroCard(
    dueCount: Int,
    todayReviewCount: Int,
    dailyGoal: Int,
    progress: Float,
    direction: LearningDirection,
    onStart: () -> Unit
) {
    val dark = isSystemInDarkTheme()
    val gradient = if (dark) {
        Brush.linearGradient(listOf(HeroGradientStartDark, HeroGradientEndDark))
    } else {
        Brush.linearGradient(listOf(HeroGradientStart, HeroGradientEnd))
    }
    Card(
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .drawBehind { drawRect(gradient) }
                .padding(24.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = direction.labelShort(),
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White.copy(alpha = 0.85f),
                    modifier = Modifier
                        .clip(RoundedCornerShape(100))
                        .background(Color.White.copy(alpha = 0.18f))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                )
                Text(
                    text = "$dueCount kart hazır",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
            Spacer(Modifier.height(20.dp))
            Text(
                text = "$todayReviewCount",
                style = MaterialTheme.typography.displayLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 72.sp
                ),
                color = Color.White
            )
            Text(
                text = "bugünkü tekrarın · hedef $dailyGoal",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White.copy(alpha = 0.85f)
            )
            Spacer(Modifier.height(18.dp))
            HeroProgressBar(progress = progress.coerceIn(0f, 1f))
            Spacer(Modifier.height(20.dp))
            androidx.compose.material3.Button(
                onClick = onStart,
                shape = RoundedCornerShape(100),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .semantics { contentDescription = "Çalışmaya başla" }
            ) {
                Text(
                    text = "Çalışmaya Başla",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(Modifier.width(8.dp))
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
            }
        }
    }
}

@Composable
private fun HeroProgressBar(progress: Float) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(8.dp)
            .clip(RoundedCornerShape(100))
            .background(Color.White.copy(alpha = 0.22f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(progress)
                .height(8.dp)
                .clip(RoundedCornerShape(100))
                .background(Color.White)
        )
    }
}

@Composable
private fun ModeCard(mode: StudyMode, onClick: () -> Unit) {
    val (bg, fg, accent) = when (mode) {
        StudyMode.Flashcard -> Triple(
            MaterialTheme.colorScheme.primaryContainer,
            MaterialTheme.colorScheme.onPrimaryContainer,
            MaterialTheme.colorScheme.primary
        )
        StudyMode.Quiz -> Triple(
            MaterialTheme.colorScheme.tertiaryContainer,
            MaterialTheme.colorScheme.onTertiaryContainer,
            MaterialTheme.colorScheme.tertiary
        )
        StudyMode.Typing -> Triple(
            MaterialTheme.colorScheme.secondaryContainer,
            MaterialTheme.colorScheme.onSecondaryContainer,
            MaterialTheme.colorScheme.secondary
        )
        StudyMode.Listening -> Triple(
            MaterialTheme.colorScheme.surfaceVariant,
            MaterialTheme.colorScheme.onSurfaceVariant,
            MaterialTheme.colorScheme.primary
        )
    }
    val subtitle = when (mode) {
        StudyMode.Flashcard -> "Kart çevir, SRS"
        StudyMode.Quiz -> "4 seçenekten seç"
        StudyMode.Typing -> "Anlamı yaz"
        StudyMode.Listening -> "Dinle, seç"
    }
    Card(
        onClick = onClick,
        modifier = Modifier.width(170.dp).height(140.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = bg),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(accent.copy(alpha = 0.22f)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = mode.emoji, fontSize = 24.sp)
            }
            Column {
                Text(
                    text = mode.labelTr,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = fg
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = fg.copy(alpha = 0.75f)
                )
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
        modifier = modifier.height(128.dp),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .width(6.dp)
                    .fillMaxHeight()
                    .background(accent)
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(14.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(accent.copy(alpha = 0.14f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = category.emoji, fontSize = 22.sp)
                }
                Column {
                    Text(
                        text = category.nameTr,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
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
    }
}

private fun LearningDirection.labelShort(): String = when (this) {
    LearningDirection.ForeignToTurkish -> "EN → TR"
    LearningDirection.TurkishToForeign -> "TR → EN"
}
