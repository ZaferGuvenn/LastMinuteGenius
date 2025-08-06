package com.fastthinkerstudios.lastminutegenius.presentation.videolist

import android.graphics.Bitmap
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp

@Composable
fun TimelineSlider(
    currentPosition: Float,
    onPositionChange: (Float) -> Unit
) {
    var containerWidth by remember { mutableStateOf(0) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .padding(horizontal = 8.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .onSizeChanged { containerWidth = it.width }
            .pointerInput(Unit) {
                detectTapGestures { tapOffset ->
                    val relative = (tapOffset.x / containerWidth).coerceIn(0f, 1f)
                    onPositionChange(relative)
                }
            }
    ) {
        if (containerWidth > 0) {
            val offsetX = (currentPosition * containerWidth).toInt()

            // Çizgi
            Box(
                modifier = Modifier
                    .offset { IntOffset(offsetX - 1, 0) }
                    .width(4.dp)
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.6f))
                    .align(Alignment.CenterStart)
            )

            // Thumb (uç nokta)
            Box(
                modifier = Modifier
                    .offset { IntOffset(offsetX - 16, 0) }
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
                    .align(Alignment.CenterStart)
            )
        }
    }
}

