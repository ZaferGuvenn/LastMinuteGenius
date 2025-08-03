package com.fastthinkerstudios.lastminutegenius.presentation.videolist

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.util.Size
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.fastthinkerstudios.lastminutegenius.domain.model.Video
import androidx.core.net.toUri

@Composable
fun VideoItem(
    video: Video,
    onDeleteClick: () -> Unit
) {
    val context = LocalContext.current
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(video.uri) {
        val parsedUri = video.uri.toUri()
        bitmap = extractVideoThumbnailCompat(context, parsedUri)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            if (bitmap != null) {
                Image(
                    bitmap = bitmap!!.asImageBitmap(),
                    contentDescription = "Video thumbnail",
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Gray)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = video.name, style = MaterialTheme.typography.titleMedium)
                Text(text = video.uri, style = MaterialTheme.typography.bodySmall, maxLines = 1)
            }

            IconButton(onClick = onDeleteClick) {
                Icon(Icons.Default.Delete, contentDescription = "Sil")
            }
        }
    }
}

fun extractVideoThumbnailCompat(context: Context, uri: Uri): Bitmap? {
    return try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
    // SDK 29 ve üzeri için ContentResolver#loadThumbnail
            context.contentResolver.loadThumbnail(uri, Size(128, 128), null)
        } else {
    // SDK 24-28 için MediaMetadataRetriever
            val retriever = MediaMetadataRetriever()
            val fd = context.contentResolver.openFileDescriptor(uri, "r")?.fileDescriptor
            if (fd != null) {
                retriever.setDataSource(fd)
                val bitmap = retriever.frameAtTime
                retriever.release()
                bitmap
            } else null
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
