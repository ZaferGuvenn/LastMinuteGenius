package com.fastthinkerstudios.lastminutegenius.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class CategoryEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Int=0,
    val parentId: Int?=null,
    val name: String

)