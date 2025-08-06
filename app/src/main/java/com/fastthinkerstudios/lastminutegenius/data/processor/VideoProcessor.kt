package com.fastthinkerstudios.lastminutegenius.data.processor

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.util.Log
import com.arthenica.ffmpegkit.FFmpegKit
import com.arthenica.ffmpegkit.ReturnCode
import kotlinx.coroutines.delay
import okio.IOException
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class VideoProcessor @Inject constructor(){

    fun extractAudioFile(context: Context, videoUri: Uri): File {
        // 1. Videoyu geçici dosyaya kopyala
        val inputFile = File.createTempFile("input", ".mp4", context.cacheDir)
        context.contentResolver.openInputStream(videoUri)?.use { input ->
            FileOutputStream(inputFile).use { out ->
                input.copyTo(out)
            }
        }

        // 2. Çıkacak ses dosyasını belirle (FLAC formatında)
        val outputFile = File(context.cacheDir, "extracted_audio.flac")
        if (outputFile.exists()) outputFile.delete()

        // 3. FLAC ses çıkarma komutu
        val command = arrayOf(
            "-y", // üzerine yaz
            "-i", inputFile.absolutePath,
            "-vn", // video çıkar
            "-ac", "1", // mono ses (daha net STT için)
            "-ar", "16000", // 16 kHz (Google STT önerisi)
            "-c:a", "flac", // FLAC codec
            outputFile.absolutePath
        )

        val session = FFmpegKit.executeWithArguments(command)
        if (!ReturnCode.isSuccess(session.returnCode)) {
            throw IOException("FFmpegKit failed: code=${session.returnCode}")
        }

        val fileSizeInMB = outputFile.length().toDouble() / (1024 * 1024)
        Log.d("AudioExtract", "🎧 FLAC dosya boyutu: %.2f MB".format(fileSizeInMB))


        return outputFile
    }


    fun extractFrames(context: Context, videoUri: Uri): List<File> {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(context, videoUri)

        val durationMs = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLong() ?: 0
        val frameCount = 5 // Mesela 5 tane al
        val intervalMs = durationMs / frameCount

        val frameFiles = mutableListOf<File>()

        for (i in 0 until frameCount) {
            val timeUs = (i * intervalMs) * 1000
            val bitmap = retriever.getFrameAtTime(timeUs, MediaMetadataRetriever.OPTION_CLOSEST)

            bitmap?.let {
                val file = File.createTempFile("frame_$i", ".jpg", context.cacheDir)
                FileOutputStream(file).use { out ->
                    it.compress(Bitmap.CompressFormat.JPEG, 90, out)
                }
                frameFiles.add(file)
            }
        }

        retriever.release()
        return frameFiles
    }


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

}