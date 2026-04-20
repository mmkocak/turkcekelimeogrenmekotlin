package com.muhammetkocak.turkcekelimeapp.ui.library

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.muhammetkocak.turkcekelimeapp.domain.model.Category
import com.muhammetkocak.turkcekelimeapp.domain.model.Word
import com.muhammetkocak.turkcekelimeapp.ui.component.CategoryChip
import com.muhammetkocak.turkcekelimeapp.ui.component.EmptyState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    initialCategoryId: Long?,
    onBack: () -> Unit,
    onAddWord: () -> Unit,
    onEditWord: (Long) -> Unit,
    onStudyWord: (Long) -> Unit,
    viewModel: LibraryViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    var detailWord by remember { mutableStateOf<Word?>(null) }

    androidx.compose.runtime.LaunchedEffect(initialCategoryId) {
        viewModel.setCategory(initialCategoryId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Kütüphane") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddWord,
                icon = { Icon(Icons.Filled.Add, contentDescription = null) },
                text = { Text("Kelime Ekle") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            OutlinedTextField(
                value = state.query,
                onValueChange = viewModel::setQuery,
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                trailingIcon = {
                    if (state.query.isNotEmpty()) {
                        IconButton(onClick = { viewModel.setQuery("") }) {
                            Icon(Icons.Filled.Close, contentDescription = "Temizle")
                        }
                    }
                },
                placeholder = { Text("Kelime ara…") },
                singleLine = true,
                shape = MaterialTheme.shapes.large,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )

            FilterChipRow(
                selected = state.filter,
                onSelect = viewModel::setFilter,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            if (state.categories.isNotEmpty()) {
                CategoryRow(
                    categories = state.categories,
                    selectedId = state.selectedCategoryId,
                    onSelect = viewModel::setCategory,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(Modifier.height(8.dp))

            if (state.loading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Yükleniyor…") }
            } else if (state.words.isEmpty()) {
                EmptyState(
                    title = "Kelime bulunamadı",
                    description = "Arama ölçütlerini değiştir ya da yeni bir kelime ekle.",
                    emoji = "🔍"
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(state.words, key = { it.id }) { word ->
                        WordRow(
                            word = word,
                            category = state.categories.firstOrNull { it.id == word.categoryId },
                            onClick = { detailWord = word },
                            onToggleFavorite = { viewModel.toggleFavorite(word) }
                        )
                    }
                }
            }
        }
    }

    detailWord?.let { word ->
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = { detailWord = null },
            sheetState = sheetState
        ) {
            WordDetailContent(
                word = word,
                onStudy = {
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        detailWord = null
                        onStudyWord(word.id)
                    }
                },
                onEdit = {
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        detailWord = null
                        onEditWord(word.id)
                    }
                },
                onDelete = {
                    viewModel.delete(word)
                    scope.launch { sheetState.hide() }.invokeOnCompletion { detailWord = null }
                },
                onToggleFavorite = {
                    viewModel.toggleFavorite(word)
                    detailWord = word.copy(isFavorite = !word.isFavorite)
                }
            )
        }
    }
}

@Composable
private fun FilterChipRow(
    selected: LibraryFilter,
    onSelect: (LibraryFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(LibraryFilter.entries.toList()) { f ->
            val label = when (f) {
                LibraryFilter.All -> "Tümü"
                LibraryFilter.Favorites -> "⭐ Favoriler"
                LibraryFilter.UserCreated -> "Benim eklediklerim"
                LibraryFilter.Mastered -> "Ustalaşıldı"
            }
            FilterChip(
                selected = selected == f,
                onClick = { onSelect(f) },
                label = { Text(label) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    }
}

@Composable
private fun CategoryRow(
    categories: List<Category>,
    selectedId: Long?,
    onSelect: (Long?) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            CategoryChip(
                emoji = "🔖",
                label = "Tüm Kategoriler",
                selected = selectedId == null,
                onClick = { onSelect(null) }
            )
        }
        items(categories, key = { it.id }) { category ->
            CategoryChip(
                emoji = category.emoji,
                label = category.nameTr,
                selected = selectedId == category.id,
                onClick = { onSelect(category.id) }
            )
        }
    }
}

@Composable
private fun WordRow(
    word: Word,
    category: Category?,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit
) {
    Card(
        onClick = onClick,
        shape = androidx.compose.foundation.shape.RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (category != null) {
                val accent = runCatching { Color(android.graphics.Color.parseColor(category.colorHex)) }
                    .getOrDefault(MaterialTheme.colorScheme.primary)
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(androidx.compose.foundation.shape.RoundedCornerShape(14.dp))
                        .background(accent.copy(alpha = 0.14f)),
                    contentAlignment = Alignment.Center
                ) { Text(category.emoji, fontSize = 24.sp) }
            }
            Column(Modifier.weight(1f)) {
                Text(
                    text = word.foreignTerm,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    maxLines = 1
                )
                Text(
                    text = word.turkishTerm,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
                if (!word.partOfSpeech.isNullOrBlank()) {
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = word.partOfSpeech.replaceFirstChar { it.titlecase() },
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .clip(androidx.compose.foundation.shape.RoundedCornerShape(100))
                            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }
            IconButton(onClick = onToggleFavorite) {
                val icon = if (word.isFavorite) Icons.Filled.Star else Icons.Outlined.StarBorder
                val tint = if (word.isFavorite) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.outline
                Icon(icon, contentDescription = if (word.isFavorite) "Favoriden çıkar" else "Favoriye ekle", tint = tint)
            }
        }
    }
}

@Composable
private fun WordDetailContent(
    word: Word,
    onStudy: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onToggleFavorite: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(
                    text = word.foreignTerm,
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = word.turkishTerm,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (!word.ipa.isNullOrBlank()) {
                    Text(
                        text = "/${word.ipa}/",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
            IconButton(onClick = onToggleFavorite) {
                val icon = if (word.isFavorite) Icons.Filled.Star else Icons.Outlined.StarBorder
                val tint = if (word.isFavorite) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.outline
                Icon(icon, contentDescription = null, tint = tint)
            }
        }
        if (!word.exampleForeign.isNullOrBlank() || !word.exampleTurkish.isNullOrBlank()) {
            Card(
                shape = MaterialTheme.shapes.medium,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    word.exampleForeign?.takeIf { it.isNotBlank() }?.let {
                        Text(text = it, style = MaterialTheme.typography.bodyMedium)
                    }
                    word.exampleTurkish?.takeIf { it.isNotBlank() }?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TextButton(onClick = onStudy, modifier = Modifier.weight(1f)) {
                Icon(Icons.Filled.PlayArrow, contentDescription = null)
                Spacer(Modifier.size(6.dp))
                Text("Şimdi çalış")
            }
            if (word.isUserCreated) {
                TextButton(onClick = onEdit, modifier = Modifier.weight(1f)) {
                    Icon(Icons.Filled.Edit, contentDescription = null)
                    Spacer(Modifier.size(6.dp))
                    Text("Düzenle")
                }
                TextButton(
                    onClick = onDelete,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Filled.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                    Spacer(Modifier.size(6.dp))
                    Text("Sil", color = MaterialTheme.colorScheme.error)
                }
            }
        }
        Spacer(Modifier.height(8.dp))
    }
}
