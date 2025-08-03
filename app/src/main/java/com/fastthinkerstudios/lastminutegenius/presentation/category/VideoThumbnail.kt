package com.fastthinkerstudios.lastminutegenius.presentation.category

import android.net.Uri
import android.widget.ImageView
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun VideoThumbnail(uri: Uri) {
    AndroidView(
        modifier = Modifier
            .size(120.dp)
            .clip(RoundedCornerShape(12.dp)),
        factory = { context ->
            ImageView(context).apply {
                setImageURI(uri)
                scaleType = ImageView.ScaleType.CENTER_CROP
            }
        }
    )
}