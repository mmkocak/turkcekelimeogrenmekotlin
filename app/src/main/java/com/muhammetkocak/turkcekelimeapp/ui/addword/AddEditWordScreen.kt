package com.muhammetkocak.turkcekelimeapp.ui.addword

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.muhammetkocak.turkcekelimeapp.ui.component.PrimaryButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditWordScreen(
    onBack: () -> Unit,
    onSaved: () -> Unit,
    viewModel: AddEditWordViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                AddEditWordEffect.Saved -> onSaved()
                is AddEditWordEffect.Error -> snackbarHostState.showSnackbar(effect.message)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (state.isEditing) "Kelime Düzenle" else "Kelime Ekle") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            OutlinedTextField(
                value = state.foreignTerm,
                onValueChange = viewModel::setForeignTerm,
                label = { Text("Yabancı kelime") },
                isError = state.foreignError,
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = state.turkishTerm,
                onValueChange = viewModel::setTurkishTerm,
                label = { Text("Türkçe karşılık") },
                isError = state.turkishError,
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            CategoryDropdown(
                categories = state.categories,
                selectedId = state.categoryId,
                isError = state.categoryError,
                onSelect = viewModel::setCategory
            )

            Text(
                text = "Tür",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            androidx.compose.foundation.layout.FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PartsOfSpeech.forEach { (raw, label) ->
                    FilterChip(
                        selected = state.partOfSpeech == raw,
                        onClick = {
                            viewModel.setPartOfSpeech(if (state.partOfSpeech == raw) null else raw)
                        },
                        label = { Text(label) }
                    )
                }
            }

            OutlinedTextField(
                value = state.exampleForeign,
                onValueChange = viewModel::setExampleForeign,
                label = { Text("Örnek cümle (yabancı)") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = state.exampleTurkish,
                onValueChange = viewModel::setExampleTurkish,
                label = { Text("Örnek cümle (Türkçe)") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = state.ipa,
                onValueChange = viewModel::setIpa,
                label = { Text("Telaffuz (IPA, opsiyonel)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            PrimaryButton(
                text = if (state.saving) "Kaydediliyor…" else "Kaydet",
                onClick = viewModel::save,
                enabled = !state.saving,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoryDropdown(
    categories: List<com.muhammetkocak.turkcekelimeapp.domain.model.Category>,
    selectedId: Long?,
    isError: Boolean,
    onSelect: (Long) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selected = categories.firstOrNull { it.id == selectedId }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
        OutlinedTextField(
            value = selected?.let { "${it.emoji} ${it.nameTr}" } ?: "Kategori seç",
            onValueChange = {},
            readOnly = true,
            isError = isError,
            label = { Text("Kategori") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(androidx.compose.material3.MenuAnchorType.PrimaryNotEditable, enabled = true)
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            categories.forEach { category ->
                DropdownMenuItem(
                    text = { Text("${category.emoji} ${category.nameTr}") },
                    onClick = {
                        onSelect(category.id)
                        expanded = false
                    }
                )
            }
        }
    }
}
