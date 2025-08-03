package com.fastthinkerstudios.lastminutegenius.domain.model

data class Video(
    val id: Int,
    val snapshot: String,
    val name: String,
    val categoryId: Int,
    val isProgressing: Boolean = false,
    val summary: String? = null,
)