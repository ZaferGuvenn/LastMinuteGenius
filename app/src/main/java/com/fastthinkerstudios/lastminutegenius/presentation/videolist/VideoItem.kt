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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun VideoItem(
    video: Video,
    onDeleteClick: () -> Unit,
    onFramesSelected: (List<Bitmap>) -> Unit // Seçilen frameleri dışarı aktarmak için
) {
    val context = LocalContext.current
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    var checked by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }
    var currentPosition by remember { mutableStateOf(0f) }
    var selectedFrames by remember { mutableStateOf<List<Bitmap>>(emptyList()) }
    var currentFrame by remember { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(video.uri) {
        val parsedUri = video.uri.toUri()
        bitmap = extractVideoThumbnailCompat(context, parsedUri)
    }

    // Timeline pozisyonu değiştiğinde frame al
    LaunchedEffect(currentPosition, expanded) {
        if (expanded) {
            withContext(Dispatchers.IO) {
                val frame = getVideoFrameAtPosition(context, video.uri.toUri(), currentPosition)
                currentFrame = frame
            }
        }
    }


    Column {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (bitmap != null) {
                    Image(
                        bitmap = bitmap!!.asImageBitmap(),
                        contentDescription = "Video thumbnail",
                        modifier = Modifier
                            .size(128.dp, 84.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(128.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.Gray))
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(text = video.name, style = MaterialTheme.typography.titleMedium)
                }

                IconButton(onClick = onDeleteClick) {
                    Icon(Icons.Default.Delete, contentDescription = "Sil")
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = checked,
                            onCheckedChange = { isChecked ->
                                checked = isChecked
                                expanded = isChecked
                            },
                        )
                        Text("Görsel seç", modifier = Modifier.padding(start = 8.dp))
                    }

                    Button(
                        onClick = { /* Özet çıkarma işlemi */ }
                    ) {
                        Text("Özet Çıkar")
                    }
                }

                if (expanded) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .animateContentSize()
                    ) {
                        // Mevcut frame önizlemesi
                        currentFrame?.let { frame ->
                            Image(
                                bitmap = frame.asImageBitmap(),
                                contentDescription = "Current frame preview",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                                    .clip(RoundedCornerShape(8.dp))
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        //ibre

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                        ) {
                            var containerWidth by remember { mutableStateOf(0) }

                            // Timeline arka planı
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .background(Color.LightGray.copy(alpha = 0.3f))
                                    .onSizeChanged { containerWidth = it.width }
                                    .pointerInput(Unit) {
                                        detectTapGestures { tapOffset ->
                                            val relativePosition = (tapOffset.x / containerWidth).coerceIn(0f, 1f)
                                            currentPosition = relativePosition
                                        }
                                    }
                            )

                            if (containerWidth > 0) {
                                val offsetX = (currentPosition * containerWidth).toInt()

                                // Sabit genişlikte ince ibre (örn: 4.dp)
                                Box(
                                    modifier = Modifier
                                        .offset { IntOffset(offsetX - 2, 0) } // merkezde dursun diye -2
                                        .width(6.dp)
                                        .fillMaxHeight()
                                        .clip(RoundedCornerShape(50))
                                        .background(Color(0xFFE1BEE7)) // açık mor çizgi
                                )

                                // Thumb gibi uç nokta
                                Box(
                                    modifier = Modifier
                                        .offset { IntOffset(offsetX - 8, 0) }
                                        .size(12.dp)
                                        .align(Alignment.CenterStart)
                                        .clip(CircleShape)
                                        .background(Color(0xFF6A1B9A)) // mor thumb
                                )
                            }
                        }


                        // Seçilen framelerin küçük görselleri
                        if (selectedFrames.isNotEmpty()) {
                            Text("Seçilen Frameler:",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(top = 8.dp))

                            LazyRow(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                itemsIndexed(selectedFrames) { index, frame ->
                                    Box(
                                        modifier = Modifier
                                            .size(80.dp)
                                    ) {
                                        Image(
                                            bitmap = frame.asImageBitmap(),
                                            contentDescription = "Selected frame",
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .clip(RoundedCornerShape(4.dp))
                                        )
                                        IconButton(
                                            onClick = {
                                                selectedFrames = selectedFrames.toMutableList().apply {
                                                    removeAt(index)
                                                }
                                            },
                                            modifier = Modifier
                                                .align(Alignment.TopEnd)
                                                .size(24.dp)
                                        ) {
                                            Icon(
                                                Icons.Default.Close,
                                                contentDescription = "Frame kaldır",
                                                tint = Color.Red,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Button(
                                onClick = {
                                    currentFrame?.let { frame ->
                                        if (selectedFrames.size < 5) {
                                            selectedFrames = selectedFrames + frame
                                        }
                                    }
                                },
                                enabled = selectedFrames.size < 5 && currentFrame != null
                            ) {
                                Text("Frame Ekle (${selectedFrames.size}/5)")
                            }

                            Button(
                                onClick = {
                                    selectedFrames = emptyList()
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.secondary
                                ),
                                enabled = selectedFrames.isNotEmpty()
                            ) {
                                Text("Tümünü Temizle")
                            }
                        }
                    }
                }
            }
        }
    }

    // Seçilen frameleri dışarı aktar
    LaunchedEffect(selectedFrames) {
        onFramesSelected(selectedFrames)
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
