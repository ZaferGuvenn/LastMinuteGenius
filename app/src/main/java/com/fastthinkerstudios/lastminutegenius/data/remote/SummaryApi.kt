package com.fastthinkerstudios.lastminutegenius.data.remote

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

}