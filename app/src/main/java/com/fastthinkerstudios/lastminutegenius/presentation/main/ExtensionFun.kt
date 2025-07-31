package com.fastthinkerstudios.lastminutegenius.presentation.main

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri

fun extractFrameFromVideo(context: Context, uri: Uri, timeMs: Long): Bitmap? {
    return try {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(context, uri)
        val frame = retriever.getFrameAtTime(timeMs * 1000) // Microseconds
        retriever.release()
        frame
    } catch (e: Exception) {
        null
    }
}

fun getVideoDuration(context: Context, uri: Uri): Long {
    val retriever = MediaMetadataRetriever()
    retriever.setDataSource(context, uri)
    val duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLong() ?: 0L
    retriever.release()
    return duration
}
