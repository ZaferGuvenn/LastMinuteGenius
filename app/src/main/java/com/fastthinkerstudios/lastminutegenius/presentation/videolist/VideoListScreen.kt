package com.fastthinkerstudios.lastminutegenius.presentation.videolist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun VideoListScreen (
    categoryId:Int,
    viewModel: VideoListViewModel= hiltViewModel()
){

    val videos by viewModel.videos.collectAsState()
    val videoStates by viewModel.videoStates.collectAsState()


    LaunchedEffect(categoryId) {
        viewModel.loadVideos(categoryId)
    }

    Column( modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            "Kategori ID: $categoryId",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 12.dp))

        if (videos.isEmpty()){
            Text("Bu kategoriye ait video yok.")
        }else{
            LazyColumn (
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ){
                items(videos){ video ->

                    VideoItem(
                        video,
                        state = videoStates[video.id]?:UiState(),
                        onDeleteClick = { viewModel.deleteVideo(video) },
                        onFramesSelected = { frames->

                            viewModel.onFramesSelected(video, frames)
                        },
                        onSummarizeClick = { viewModel.summarizeVideo(video) },
                        onShowSummaryClick = {
                            println("video.summary")
                            println(video.summary)

                        }
                    )
                }
            }
        }


    }

}