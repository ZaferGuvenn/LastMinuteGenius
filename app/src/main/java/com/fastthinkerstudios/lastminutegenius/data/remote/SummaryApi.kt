package com.fastthinkerstudios.lastminutegenius.data.remote

import com.fastthinkerstudios.lastminutegenius.domain.model.SummaryRequest
import com.fastthinkerstudios.lastminutegenius.domain.model.SummaryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface SummaryApi {

    @Multipart
    @POST("summarize")
    suspend fun summarizeAudio(
        @Part audio: MultipartBody.Part,
        @Part("language_code") languageCode: RequestBody
    ): Response<SummaryResponse>
}