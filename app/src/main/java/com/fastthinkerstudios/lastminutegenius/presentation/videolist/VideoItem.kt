package com.fastthinkerstudios.lastminutegenius.presentation.videolist

import android.R.attr.translationX
import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.util.Log
import android.util.Size
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import com.fastthinkerstudios.lastminutegenius.domain.model.Video
import androidx.core.net.toUri
import com.fastthinkerstudios.lastminutegenius.util.fromBase64ToBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun VideoItem(
    video: Video,
    state: UiState,
    onDeleteClick: () -> Unit,
    onFramesSelected: (List<Bitmap>) -> Unit,
    onSummarizeClick: () -> Unit,
    onShowSummaryClick: () -> Unit,
    onTakeQuizClick: () -> Unit
) {
    val context = LocalContext.current
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    var checked by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }
    var currentPosition by remember { mutableStateOf(0f) }
    var selectedFrames by remember { mutableStateOf<List<Bitmap>>(emptyList()) }
    var currentFrame by remember { mutableStateOf<Bitmap?>(null) }


    // Load thumbnail
    LaunchedEffect(video.uri) {
        bitmap = extractVideoThumbnailCompat(context, video.uri.toUri())
    }

    // Load frame preview when position changes
    LaunchedEffect(currentPosition, expanded) {
        if (expanded) {
            currentFrame = withContext(Dispatchers.IO) {
                getVideoFrameAtPosition(context, video.uri.toUri(), currentPosition)
            }
        }
    }

    // Load saved snapshots
    LaunchedEffect(video.snapshots) {
        if (video.snapshots.isNotEmpty()) {
            selectedFrames = video.snapshots.mapNotNull { it.fromBase64ToBitmap() }
            checked = true
            expanded = true
        }
    }

    LaunchedEffect(selectedFrames) {
        onFramesSelected(selectedFrames)
    }

    Column {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column {
                TopRow(
                    video = video,
                    bitmap = bitmap,
                    isLoading = state.isLoading,
                    onDeleteClick = onDeleteClick
                )

                if (video.summary.isNullOrBlank()) {
                    SummaryControlSection(
                        checked = checked,
                        onCheckedChange = {
                            checked = it
                            expanded = it
                        },
                        onSummarizeClick = onSummarizeClick
                    )

                    if (expanded) {
                        FrameSelector(
                            currentFrame = currentFrame,
                            selectedFrames = selectedFrames,
                            currentPosition = currentPosition,
                            onPositionChange = { currentPosition = it },
                            onAddFrame = {
                                currentFrame?.let {
                                    if (selectedFrames.size < 5) {
                                        selectedFrames = selectedFrames + it
                                    }
                                }
                            },
                            onClearAll = { selectedFrames = emptyList() },
                            onRemoveFrameAt = { index ->
                                selectedFrames = selectedFrames.toMutableList().apply {
                                    removeAt(index)
                                }
                            }
                        )
                    }
                } else {
                    SummarySection(
                        onShowSummaryClick,
                        onTakeQuizClick
                    )
                }
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

// Bu fonksiyonu VideoItem composable'ının dışına koyun
suspend fun getVideoFrameAtPosition(context: Context, uri: Uri, position: Float): Bitmap? {
    val mediaMetadataRetriever = MediaMetadataRetriever()
    return try {
        mediaMetadataRetriever.setDataSource(context, uri)
        val duration = mediaMetadataRetriever.extractMetadata(
            MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLong() ?: 0L
        val timeUs = (position * duration * 1000).toLong()
        mediaMetadataRetriever.getFrameAtTime(timeUs, MediaMetadataRetriever.OPTION_CLOSEST)
    } catch (e: Exception) {
        null
    } finally {
        mediaMetadataRetriever.release()
    }
}
