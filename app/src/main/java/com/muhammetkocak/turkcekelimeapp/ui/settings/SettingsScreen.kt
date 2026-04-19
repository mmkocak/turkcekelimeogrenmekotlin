package com.muhammetkocak.turkcekelimeapp.ui.settings

import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.muhammetkocak.turkcekelimeapp.domain.model.LearningDirection
import com.muhammetkocak.turkcekelimeapp.ui.theme.ThemeMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val prefs by viewModel.state.collectAsStateWithLifecycle()
    var confirmReset by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ayarlar") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Geri")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            SectionLabel("Görünüm")
            ThemeModeRow(selected = prefs.themeMode, onSelect = viewModel::setThemeMode)
            ToggleRow(
                label = "Dinamik renk",
                hint = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) "Android 12+ duvar kağıdı tabanlı renkler" else "Android 12 öncesinde desteklenmez",
                checked = prefs.dynamicColorEnabled,
                enabled = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S,
                onToggle = viewModel::setDynamicColor
            )

            HorizontalDivider()
            SectionLabel("Çalışma")
            DirectionRow(selected = prefs.primaryDirection, onSelect = viewModel::setDirection)
            Column {
                Text("Günlük hedef: ${prefs.dailyGoal} kart", style = MaterialTheme.typography.bodyMedium)
                Slider(
                    value = prefs.dailyGoal.toFloat(),
                    onValueChange = { viewModel.setDailyGoal(it.toInt()) },
                    valueRange = 5f..100f,
                    steps = 18
                )
            }
            ToggleRow(
                label = "Seslendirme (TTS)",
                hint = "Kelimeleri cihaz TTS motoruyla dinle",
                checked = prefs.ttsEnabled,
                onToggle = viewModel::setTtsEnabled
            )
            ToggleRow(
                label = "Haptik geri bildirim",
                hint = "Cevap ve kart geçişlerinde titreşim",
                checked = prefs.hapticsEnabled,
                onToggle = viewModel::setHapticsEnabled
            )

            HorizontalDivider()
            SectionLabel("Veri")
            TextButton(onClick = { confirmReset = true }) {
                Text("Tüm ilerlemeyi sıfırla", color = MaterialTheme.colorScheme.error)
            }

            HorizontalDivider()
            SectionLabel("Hakkında")
            Text(
                text = "Türkçe Kelime Öğrenme · v1.0",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }

    if (confirmReset) {
        AlertDialog(
            onDismissRequest = { confirmReset = false },
            title = { Text("İlerlemeyi sıfırla?") },
            text = { Text("Streak ve çalışma geçmişi sıfırlanır. Kelimelerine dokunulmaz.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.resetProgress()
                    confirmReset = false
                }) { Text("Evet, sıfırla", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { confirmReset = false }) { Text("Vazgeç") }
            }
        )
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
        color = MaterialTheme.colorScheme.primary
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ThemeModeRow(selected: ThemeMode, onSelect: (ThemeMode) -> Unit) {
    val modes = ThemeMode.entries
    SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
        modes.forEachIndexed { index, mode ->
            SegmentedButton(
                selected = mode == selected,
                onClick = { onSelect(mode) },
                shape = SegmentedButtonDefaults.itemShape(index = index, count = modes.size),
                label = {
                    Text(
                        text = when (mode) {
                            ThemeMode.System -> "Sistem"
                            ThemeMode.Light -> "Açık"
                            ThemeMode.Dark -> "Koyu"
                        }
                    )
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DirectionRow(selected: LearningDirection, onSelect: (LearningDirection) -> Unit) {
    val entries = LearningDirection.entries
    SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
        entries.forEachIndexed { index, dir ->
            SegmentedButton(
                selected = dir == selected,
                onClick = { onSelect(dir) },
                shape = SegmentedButtonDefaults.itemShape(index = index, count = entries.size),
                label = {
                    Text(
                        text = when (dir) {
                            LearningDirection.ForeignToTurkish -> "EN → TR"
                            LearningDirection.TurkishToForeign -> "TR → EN"
                        }
                    )
                }
            )
        }
    }
}

@Composable
private fun ToggleRow(
    label: String,
    hint: String? = null,
    checked: Boolean,
    enabled: Boolean = true,
    onToggle: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(label, style = MaterialTheme.typography.bodyLarge)
            if (!hint.isNullOrBlank()) {
                Text(hint, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        Switch(checked = checked, onCheckedChange = onToggle, enabled = enabled)
    }
}
