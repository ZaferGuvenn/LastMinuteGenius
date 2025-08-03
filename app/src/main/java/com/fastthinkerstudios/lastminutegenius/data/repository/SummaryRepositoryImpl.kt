package com.fastthinkerstudios.lastminutegenius.data.repository

import com.fastthinkerstudios.lastminutegenius.data.remote.SummaryApi
import com.fastthinkerstudios.lastminutegenius.domain.repository.SummaryRepository
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject

class SummaryRepositoryImpl @Inject constructor(private val api: SummaryApi): SummaryRepository {

    override suspend fun uploadAudioForSummary(
        audioFile: File,
        languageCodeStr: String,
        frames: List<File>?
    ): String {
        val audioPart = MultipartBody.Part.createFormData(
            "audio", audioFile.name, audioFile.asRequestBody("audio/wav".toMediaType())
        )
        val languageCode = languageCodeStr.toRequestBody("text/plain".toMediaType())

        val frameParts = frames?.map { file ->
            MultipartBody.Part.createFormData(
                "frames", file.name, file.asRequestBody("image/jpeg".toMediaType())
            )
        } ?: emptyList()

        val response = api.summarizeAudioWithFrames(audioPart, languageCode, frameParts)

        if (response.isSuccessful) {
            return response.body()?.summary ?: "Özet bulunamadı"
        } else {
            throw okio.IOException("API Hatası: ${response.code()}")
        }
    }

}