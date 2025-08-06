package com.fastthinkerstudios.lastminutegenius.presentation.category

import android.net.Uri
import android.widget.ImageView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun VideoThumbnail(uri: Uri) {
    Card(
        modifier = Modifier
            .size(width = 120.dp, height = 80.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                ImageView(context).apply {
                    setImageURI(uri)
                    scaleType = ImageView.ScaleType.CENTER_CROP
                }
            }
        )
    }
}
