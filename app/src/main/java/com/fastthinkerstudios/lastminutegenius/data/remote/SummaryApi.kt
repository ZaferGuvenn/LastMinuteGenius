package com.fastthinkerstudios.lastminutegenius.data.remote

import com.fastthinkerstudios.lastminutegenius.domain.model.SummaryRequest
import com.fastthinkerstudios.lastminutegenius.domain.model.SummaryResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface SummaryApi {

    @POST("summarize")
    suspend fun summarize(@Body request: SummaryRequest): SummaryResponse
}