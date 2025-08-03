package com.fastthinkerstudios.lastminutegenius.domain.model

data class Video(
    val id: Int=0,
    val snapshots: List<String>,
    val name: String,
    val categoryId: Int,
    val isProgressing: Boolean = false,
    val summary: String? = null,
    val uri: String,
)