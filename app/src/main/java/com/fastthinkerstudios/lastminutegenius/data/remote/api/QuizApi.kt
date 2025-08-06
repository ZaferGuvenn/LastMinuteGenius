package com.fastthinkerstudios.lastminutegenius.data.remote.api

import com.fastthinkerstudios.lastminutegenius.data.remote.dto.QuizResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface QuizApi {

    @FormUrlEncoded
    @POST("/generate_quiz")
    suspend fun generateQuiz(
        @Field("summary") summary: String
    ): QuizResponse
}