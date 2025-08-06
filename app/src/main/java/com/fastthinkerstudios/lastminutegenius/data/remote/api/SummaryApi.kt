package com.fastthinkerstudios.lastminutegenius.data.remote.api

import com.fastthinkerstudios.lastminutegenius.data.remote.dto.SummaryResponseDto
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface SummaryApi {

    @Multipart
    @POST("summarize")
    suspend fun summarizeAudioWithFrames(
        @Part audio: MultipartBody.Part,
        @Part("language_code") languageCode: RequestBody,
        @Part frames: List<MultipartBody.Part> // Opsiyonel frame dosyaları
    ): Response<SummaryResponseDto>


    // yeni backende gcs üzerinden link ile ses dosyasını gönderme
    // ( büyük dosyalarda sorun yaşamamak için.)
    @Multipart
    @POST("summarize_by_gcs")
    suspend fun summarizeFromGcsUri(
        @Part("gcs_uri") gcsUri: RequestBody,
        @Part("language_code") languageCode: RequestBody,
        @Part frames: List<MultipartBody.Part> = emptyList()
    ): Response<SummaryResponseDto>
}