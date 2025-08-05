package com.fastthinkerstudios.lastminutegenius.presentation.videolist

import android.graphics.Bitmap
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material3.ButtonDefaults
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

@Composable
fun FrameSelector(
    currentFrame: Bitmap?,
    selectedFrames: List<Bitmap>,
    currentPosition: Float,
    onPositionChange: (Float) -> Unit,
    onAddFrame: () -> Unit,
    onClearAll: () -> Unit,
    onRemoveFrameAt: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .animateContentSize()
    ) {
        currentFrame?.let {
            Image(
                bitmap = it.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // timeline + thumb
        TimelineSlider(currentPosition = currentPosition, onPositionChange = onPositionChange)

        if (selectedFrames.isNotEmpty()) {
            Text("Seçilen Frameler:", style = MaterialTheme.typography.bodyMedium)
            LazyRow(
                modifier = Modifier.padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(selectedFrames) { index, frame ->
                    Box(modifier = Modifier.size(80.dp)) {
                        Image(
                            bitmap = frame.asImageBitmap(),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(4.dp))
                        )
                        IconButton(
                            onClick = { onRemoveFrameAt(index) },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .size(24.dp)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = null, tint = Color.Red)
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
            Button(onClick = onAddFrame, enabled = selectedFrames.size < 5 && currentFrame != null) {
                Text("Frame Ekle (${selectedFrames.size}/5)")
            }

            Button(
                onClick = onClearAll,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                enabled = selectedFrames.isNotEmpty()
            ) {
                Text("Tümünü Temizle")
            }
        }
    }
}
