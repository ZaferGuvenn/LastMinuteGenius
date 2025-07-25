package com.fastthinkerstudios.lastminutegenius.presentation.main

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun MainScreen(viewModel:MainViewModel = viewModel()) {

    val uiState by viewModel.uiState.collectAsState()

    val videoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->

            uri?.let { viewModel.onVideoSelected(it)}
        }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ){

        when{
            uiState.isLoading -> {
                CircularProgressIndicator()
                Text("Video işleniyor...")
            }

            uiState.summary.isNotBlank() -> {
                Text("Video Özeti", style = MaterialTheme.typography.titleMedium)
                Text(uiState.summary)
            }

            else -> {
                Button(onClick = {videoPicker.launch("video/*")}) {
                    Text("Videoyu Seç")
                }
            }
        }


    }


}