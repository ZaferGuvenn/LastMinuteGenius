package com.fastthinkerstudios.lastminutegenius.presentation.videolist


import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import com.fastthinkerstudios.lastminutegenius.domain.model.Video


@Composable
fun TopRow(video: Video, bitmap: Bitmap?, isLoading: Boolean, onDeleteClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(128.dp, 84.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            when {
                isLoading -> CircularProgressIndicator(modifier = Modifier.size(24.dp))
                bitmap != null -> Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize()
                )
                else -> Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Gray)
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = video.name,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.weight(1f)
        )

        IconButton(onClick = onDeleteClick) {
            Icon(Icons.Default.Delete, contentDescription = "Sil")
        }
    }
}

