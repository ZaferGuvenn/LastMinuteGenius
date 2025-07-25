package com.fastthinkerstudios.lastminutegenius.data.remote

import com.fastthinkerstudios.lastminutegenius.domain.model.SummaryRequest
import retrofit2.http.Body
import retrofit2.http.POST
import javax.inject.Inject


class SummaryRepository @Inject constructor(private val api:SummaryApi) {
    suspend fun getSummary(text: String): String? {
        return try{
            api.summarize(SummaryRequest(text)).summary
        } catch (e: Exception){
            e.printStackTrace()
            null
        }
    }
}