package com.fastthinkerstudios.lastminutegenius.data.remote

import com.fastthinkerstudios.lastminutegenius.domain.model.SummaryRequest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okio.IOException
import retrofit2.http.Body
import retrofit2.http.POST
import java.io.File
import javax.inject.Inject


class SummaryRepository @Inject constructor(private val api:SummaryApi) {

    suspend fun uploadAudioForSummary(audioFile: File, languageCodeStr: String = "tr-TR"): String {

        val requestFile = audioFile.asRequestBody("audio/wav".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("audio", audioFile.name,requestFile)

        val languageCode = languageCodeStr.toRequestBody("text/plain".toMediaTypeOrNull())

        val response = api.summarizeAudio(body, languageCode)

        if (response.isSuccessful) {
            return response.body()?.summary ?:"Boş yanıt"
        }else{
            throw IOException("Özet alınamadı: ${response.code()} - ${response.errorBody()?.string()}")
        }

    }
}