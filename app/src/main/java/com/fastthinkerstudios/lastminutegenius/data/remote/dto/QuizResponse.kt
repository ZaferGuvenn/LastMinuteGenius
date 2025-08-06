package com.fastthinkerstudios.lastminutegenius.data.remote.dto

import com.google.gson.annotations.SerializedName

data class QuizResponse(
    @SerializedName("quiz")
    val quiz: String // Bu JSON string olarak geliyor, parse ediyoruz
)