package com.fastthinkerstudios.lastminutegenius.presentation.videolist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.fastthinkerstudios.lastminutegenius.presentation.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoListScreen (
    navController: NavHostController,
    categoryId:Int,
    onShowSummaryClicked: (Int) -> Unit,
    onTakeQuizClicked: (Int) -> Unit,
    viewModel: VideoListViewModel= hiltViewModel()
){

    val videos by viewModel.videos.collectAsState()
    val videoStates by viewModel.videoStates.collectAsState()


    LaunchedEffect(categoryId) {
        viewModel.loadVideos(categoryId)
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Videolar") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {

            if (videos.isEmpty()) {
                Text("Bu kategoriye ait video yok.")
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    items(videos) { video ->
                        VideoItem(
                            video = video,
                            state = videoStates[video.id] ?: UiState(),
                            onDeleteClick = { viewModel.deleteVideo(video) },
                            onFramesSelected = { frames ->
                                viewModel.onFramesSelected(video, frames)
                            },
                            onSummarizeClick = { viewModel.summarizeVideo(video) },
                            onShowSummaryClick = { onShowSummaryClicked(video.id) },
                            onTakeQuizClick = { onTakeQuizClicked(video.id)}
                        )
                    }
                }
            }
        }
    }
}
