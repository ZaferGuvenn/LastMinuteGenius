package com.fastthinkerstudios.lastminutegenius.presentation.category

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun CategoryScreen(
    viewModel: CategoryViewModel = hiltViewModel(),
    onVideoAddClicked: (Int) -> Unit
) {
    val categories by viewModel.categories.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var showCategoryPickerDialog by remember { mutableStateOf(false) }
    var fabExpanded by remember { mutableStateOf(false) }

    val selectedVideos by viewModel.selectedVideos.collectAsState()

    Scaffold(
        floatingActionButton = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.End
            ) {
                if (fabExpanded) {
                    ExtendedFloatingActionButton(
                        icon = { Icon(Icons.Default.Category, contentDescription = null) },
                        text = { Text("Kategori Ekle") },
                        onClick = {
                            fabExpanded = false
                            showAddDialog = true
                        }
                    )
                    ExtendedFloatingActionButton(
                        icon = { Icon(Icons.Default.VideoLibrary, contentDescription = null) },
                        text = { Text("Video Ekle") },
                        onClick = {
                            fabExpanded = false
                            showCategoryPickerDialog = true
                        }
                    )
                }

                FloatingActionButton(onClick = { fabExpanded = !fabExpanded }) {
                    Icon(Icons.Default.Add, contentDescription = "Add")
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            if (selectedVideos.isNotEmpty()) {
                SelectedVideosSection(videos = selectedVideos)
            }
            when {
                isLoading -> CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                error != null -> Text("Hata: $error")
                categories.isEmpty() -> Text("Hiç kategori yok.", modifier = Modifier.padding(16.dp))
                else -> {
                    LazyColumn(contentPadding = PaddingValues(16.dp)) {
                        items(categories) { category ->
                            CategoryItem(
                                category = category,
                                onDeleteClick = { viewModel.deleteCategory(category) },
                                onClicked = {}
                            )
                        }
                    }


                }
            }
        }

        if (showAddDialog) {
            AddCategoryDialog(
                onDismiss = { showAddDialog = false },
                onConfirm = { name ->
                    viewModel.addCategory(name)
                    showAddDialog = false
                }
            )
        }

        if (showCategoryPickerDialog) {
            SelectCategoryDialog(
                categories = categories,
                onDismiss = { showCategoryPickerDialog = false },
                onCategorySelected = { category ->
                    showCategoryPickerDialog = false
                    onVideoAddClicked(category.id)
                }
            )
        }
    }
}