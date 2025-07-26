package com.fastthinkerstudios.lastminutegenius.data.processor

import android.content.Context
import android.net.Uri
import com.arthenica.ffmpegkit.FFmpegKit
import com.arthenica.ffmpegkit.ReturnCode
import kotlinx.coroutines.delay
import okio.IOException
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class VideoProcessor @Inject constructor(){

    fun extractAudioFile(context: Context, videoUri: Uri): File {

        //videoyu geçici dosyaya kopyala
        val inputFile = File.createTempFile("input", ".mp4", context.cacheDir)
        context.contentResolver.openInputStream(videoUri)?.use { input ->
            FileOutputStream(inputFile).use { out ->
                input.copyTo(out)
            }
        }

        //çıkacak ses dosyasını belirle
        val outputFile = File(context.cacheDir, "extracted_audio.wav")
        if (outputFile.exists()) outputFile.delete()

        val command = arrayOf(
            "-y", // üzerine yaz
            "-i", inputFile.absolutePath,
            "-vn", //no video
            "-acodec", "pcm_s16le", //16 bit PCM
            "-ar", "16000", // 16kHz
            "-ac", "1", //mono
            outputFile.absolutePath
        )

        val session = FFmpegKit.executeWithArguments(command)
        if (!ReturnCode.isSuccess(session.returnCode)) {
            throw IOException("FFmpegKit failed: code=${session.returnCode}")
        }

        return outputFile
    }
}