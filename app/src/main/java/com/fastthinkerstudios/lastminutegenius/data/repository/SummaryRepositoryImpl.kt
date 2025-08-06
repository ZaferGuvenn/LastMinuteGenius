package com.fastthinkerstudios.lastminutegenius.data.repository

import com.fastthinkerstudios.lastminutegenius.data.remote.api.SummaryApi
import com.fastthinkerstudios.lastminutegenius.domain.repository.SummaryRepository
import com.fastthinkerstudios.lastminutegenius.util.GcsUploader
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.IOException
import javax.inject.Inject

class SummaryRepositoryImpl @Inject constructor(private val api: SummaryApi): SummaryRepository {

    override suspend fun uploadAudioUriForSummary(
        gcsUri: String,
        languageCode: String,
        frames: List<File>?
    ): String {
        val languagePart = languageCode.toRequestBody("text/plain".toMediaType())
        val gcsUriPart = gcsUri.toRequestBody("text/plain".toMediaType())

        val frameParts = frames?.map { file ->
            MultipartBody.Part.createFormData(
                "frames", file.name, file.asRequestBody("image/jpeg".toMediaType())
            )
        } ?: emptyList()

        val response = api.summarizeFromGcsUri(gcsUriPart, languagePart, frameParts)

        if (response.isSuccessful) {
            return response.body()?.summary ?: "Özet bulunamadı"
        } else {
            throw IOException("API Hatası: ${response.code()}")
        }
    }

}