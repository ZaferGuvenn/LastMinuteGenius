package com.fastthinkerstudios.lastminutegenius.data.processor

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.delay
import javax.inject.Inject

class VideoProcessor @Inject constructor(){
    suspend fun extractTextFromVideo(context: Context, uri: Uri): String{
        // videodan ses alıp speech to text yapılacak
        delay(2000)
        return "Merhaba bu bir test metindir. Bu videoda anlatılan konular vs ne ise onları burada göreceksin."
    }
}