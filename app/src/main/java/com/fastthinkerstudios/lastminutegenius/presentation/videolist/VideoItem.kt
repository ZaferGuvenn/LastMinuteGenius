package com.fastthinkerstudios.lastminutegenius.presentation.videolist

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fastthinkerstudios.lastminutegenius.domain.model.Video

@Composable
fun VideoItem(video: Video) {

    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier= Modifier.padding(16.dp)) {
            Text(text = video.name, style = MaterialTheme.typography.titleMedium)
            Text(text = video.uri, style = MaterialTheme.typography.bodySmall, maxLines = 1)
        }
    }
}