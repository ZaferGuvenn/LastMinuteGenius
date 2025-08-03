package com.fastthinkerstudios.lastminutegenius.domain.model

data class Category(
    val id: Int,
    val parentId: Int? = null,
    val name: String
)