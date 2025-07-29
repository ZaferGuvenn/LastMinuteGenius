package com.fastthinkerstudios.lastminutegenius.data.remote

import com.fastthinkerstudios.lastminutegenius.domain.model.SummaryRequest
import okhttp3.MediaType.Companion.toMediaType
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

    suspend fun uploadAudioForSummary(
        audioFile: File,
        languageCodeStr: String,
        frames: List<File>?
    ): String {
        val audioPart = MultipartBody.Part.createFormData(
            "audio", audioFile.name, audioFile.asRequestBody("audio/wav".toMediaType())
        )
        val languageCode = languageCodeStr.toRequestBody("text/plain".toMediaType())

        val frameParts = frames?.mapIndexed { index, file ->
            MultipartBody.Part.createFormData(
                "frame$index", file.name, file.asRequestBody("image/jpeg".toMediaType())
            )
        } ?: emptyList()

        val response = api.summarizeAudioWithFrames(audioPart, languageCode, frameParts)

        if (response.isSuccessful) {
            return response.body()?.summary ?: "Özet bulunamadı"
        } else {
            throw IOException("API Hatası: ${response.code()}")
        }
    }

}