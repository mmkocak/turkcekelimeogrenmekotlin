package com.muhammetkocak.turkcekelimeapp.ui.stats

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.muhammetkocak.turkcekelimeapp.core.datetime.DAY_MILLIS
import com.muhammetkocak.turkcekelimeapp.data.local.dao.DailyReviewAggregate
import com.muhammetkocak.turkcekelimeapp.domain.model.CardMastery
import com.muhammetkocak.turkcekelimeapp.domain.usecase.StatsSnapshot
import com.muhammetkocak.turkcekelimeapp.ui.component.SectionHeader
import com.muhammetkocak.turkcekelimeapp.ui.theme.MasteryLearning
import com.muhammetkocak.turkcekelimeapp.ui.theme.MasteryMastered
import com.muhammetkocak.turkcekelimeapp.ui.theme.MasteryNew
import com.muhammetkocak.turkcekelimeapp.ui.theme.MasteryReview
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(
    onBack: () -> Unit,
    viewModel: StatsViewModel = hiltViewModel()
) {
    val snapshot by viewModel.state.collectAsStateWithLifecycle()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("İstatistikler") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Geri")
                    }
                }
            )
        }
    ) { padding ->
        if (snapshot == null) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("Yükleniyor…")
            }
            return@Scaffold
        }
        val s = snapshot!!
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { StreakRow(current = s.currentStreak, longest = s.longestStreak, total = s.weeklyTotalReviews) }
            item { SectionHeader(title = "Ustalık dağılımı") }
            item { MasteryDonut(snapshot = s) }
            item { SectionHeader(title = "Son 6 hafta") }
            item { WeeklyBars(aggregates = s.weeklyAggregates) }
            item { SectionHeader(title = "Aktivite") }
            item { Heatmap(snapshot = s) }
        }
    }
}

@Composable
private fun StreakRow(current: Int, longest: Int, total: Int) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        StatTile(label = "Şimdiki seri", value = "$current", emoji = "🔥", modifier = Modifier.weight(1f))
        StatTile(label = "En uzun seri", value = "$longest", emoji = "🏆", modifier = Modifier.weight(1f))
        StatTile(label = "Bu dönem", value = "$total", emoji = "📈", modifier = Modifier.weight(1f))
    }
}

@Composable
private fun StatTile(label: String, value: String, emoji: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            Modifier.fillMaxWidth().padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(text = emoji, fontSize = 20.sp)
            Text(text = value, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun MasteryDonut(snapshot: StatsSnapshot) {
    val total = snapshot.totalCards.coerceAtLeast(1)
    val segments = listOf(
        CardMastery.New to MasteryNew,
        CardMastery.Learning to MasteryLearning,
        CardMastery.Review to MasteryReview,
        CardMastery.Mastered to MasteryMastered
    )
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Canvas(modifier = Modifier.size(140.dp)) {
            var startAngle = -90f
            val stroke = Stroke(width = 28f, cap = StrokeCap.Butt)
            val inset = stroke.width / 2
            val rectSize = Size(size.width - stroke.width, size.height - stroke.width)
            val topLeft = Offset(inset, inset)
            segments.forEach { (mastery, color) ->
                val count = snapshot.masteryCounts[mastery] ?: 0
                val sweep = 360f * (count.toFloat() / total)
                drawArc(
                    color = color,
                    startAngle = startAngle,
                    sweepAngle = sweep,
                    useCenter = false,
                    topLeft = topLeft,
                    size = rectSize,
                    style = stroke
                )
                startAngle += sweep
            }
        }
        Column(verticalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.weight(1f)) {
            segments.forEach { (mastery, color) ->
                val count = snapshot.masteryCounts[mastery] ?: 0
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(Modifier.size(10.dp).background(color, shape = androidx.compose.foundation.shape.CircleShape))
                    Text(
                        text = "${mastery.labelTr()} · $count",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
private fun WeeklyBars(aggregates: List<DailyReviewAggregate>) {
    val last7 = lastNDaysAggregates(aggregates, 7)
    val max = (last7.maxOfOrNull { it.reviewCount } ?: 0).coerceAtLeast(1)
    val barColor = MaterialTheme.colorScheme.primary
    val trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
    val dayFmt = remember7DayFormatter()
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp).height(140.dp),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            last7.forEach { day ->
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "${day.reviewCount}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Canvas(modifier = Modifier.fillMaxWidth().height(90.dp)) {
                        val ratio = day.reviewCount.toFloat() / max
                        val barHeight = size.height * ratio
                        drawRoundRect(
                            color = trackColor,
                            topLeft = Offset(0f, 0f),
                            size = Size(size.width, size.height),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(16f, 16f)
                        )
                        drawRoundRect(
                            color = barColor,
                            topLeft = Offset(0f, size.height - barHeight),
                            size = Size(size.width, barHeight),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(16f, 16f)
                        )
                    }
                    Text(
                        text = dayFmt.format(Date(day.dayEpoch)),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
        }
    }
}

@Composable
private fun Heatmap(snapshot: StatsSnapshot) {
    val days = (0 until 42).map { i -> snapshot.windowStartEpoch + i * DAY_MILLIS }
    val map = snapshot.weeklyAggregates.associateBy { it.dayEpoch }
    val max = (map.values.maxOfOrNull { it.reviewCount } ?: 0).coerceAtLeast(1)
    val accent = MaterialTheme.colorScheme.primary
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            for (week in 0 until 6) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    for (d in 0 until 7) {
                        val epoch = days[week * 7 + d]
                        val count = map[epoch]?.reviewCount ?: 0
                        val alpha = if (count == 0) 0.08f else (0.2f + 0.8f * (count.toFloat() / max)).coerceIn(0.2f, 1f)
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .background(accent.copy(alpha = alpha), shape = MaterialTheme.shapes.small)
                        )
                    }
                }
            }
        }
    }
}

private fun CardMastery.labelTr(): String = when (this) {
    CardMastery.New -> "Yeni"
    CardMastery.Learning -> "Öğreniliyor"
    CardMastery.Review -> "Tekrar"
    CardMastery.Mastered -> "Ustalaşıldı"
}

private fun lastNDaysAggregates(all: List<DailyReviewAggregate>, n: Int): List<DailyReviewAggregate> {
    if (all.isEmpty()) {
        val now = System.currentTimeMillis()
        val today = (now / DAY_MILLIS) * DAY_MILLIS
        return (n - 1 downTo 0).map { DailyReviewAggregate(dayEpoch = today - it * DAY_MILLIS, reviewCount = 0, correctCount = 0) }
    }
    val map = all.associateBy { it.dayEpoch }
    val last = all.last().dayEpoch
    return (n - 1 downTo 0).map { i ->
        val epoch = last - i * DAY_MILLIS
        map[epoch] ?: DailyReviewAggregate(dayEpoch = epoch, reviewCount = 0, correctCount = 0)
    }
}

@Composable
private fun remember7DayFormatter(): SimpleDateFormat =
    androidx.compose.runtime.remember { SimpleDateFormat("EEE", Locale("tr", "TR")) }
