package com.fastthinkerstudios.lastminutegenius.presentation.main

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.fastthinkerstudios.lastminutegenius.data.processor.VideoProcessor

@Composable
fun FrameSelector(
    videoProcessor: VideoProcessor,
    videoUri: Uri,
    selectedFrames: List<Bitmap>,
    onAddFrame: (Bitmap) -> Unit,
    onRemoveFrame: (Int) -> Unit
) {
    val context = LocalContext.current
    var currentFrameBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var currentTime by remember { mutableStateOf(0L) }


    Column(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
        Text("📸 Öne Çıkarılacak Görselleri Seç (max 5)", style = MaterialTheme.typography.bodyMedium)

        Slider(
            value = currentTime.toFloat(),
            onValueChange = {
                currentTime = it.toLong()
                currentFrameBitmap = videoProcessor.extractFrameFromVideo(context, videoUri, currentTime)
            },
            valueRange = 0f..videoProcessor.getVideoDuration(context, videoUri).toFloat(),
            modifier = Modifier.fillMaxWidth()
        )

        currentFrameBitmap?.let { bitmap ->
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .padding(8.dp)
            )
        }

        Button(
            onClick = {
                currentFrameBitmap?.let {
                    if (selectedFrames.size < 5) onAddFrame(it)
                }
            },
            enabled = selectedFrames.size < 5 && currentFrameBitmap != null
        ) {
            Text("Görsel Ekle")
        }

        if (selectedFrames.isNotEmpty()) {
            LazyRow {
                itemsIndexed(selectedFrames) { index, bmp ->
                    Box(modifier = Modifier.padding(8.dp)) {
                        Image(
                            bitmap = bmp.asImageBitmap(),
                            contentDescription = null,
                            modifier = Modifier
                                .size(100.dp)
                                .clip(RoundedCornerShape(8.dp))
                        )
                        IconButton(
                            onClick = { onRemoveFrame(index) },
                            modifier = Modifier.align(Alignment.TopEnd)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Sil")
                        }
                    }
                }
            }
        }
    }
}
