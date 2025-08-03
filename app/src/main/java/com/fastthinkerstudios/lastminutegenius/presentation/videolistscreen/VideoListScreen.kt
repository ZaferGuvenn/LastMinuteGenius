package com.fastthinkerstudios.lastminutegenius.presentation.videolistscreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun VideoListScreen (
    categoryId:Int,
    viewModel: VideoListViewModel= hiltViewModel()
){

    val videos by viewModel.videos.collectAsState()


    LaunchedEffect(categoryId) {
        viewModel.loadVideos(categoryId)
    }

    Column( modifier = Modifier.fillMaxSize()) {
        Text("Kategori $categoryId için videolar", style = MaterialTheme.typography.headlineSmall)

        LazyColumn {
            items(videos){ video ->

                Text(video.name) // Şimdilik sadece adı gösterelim

            }
        }
    }

}