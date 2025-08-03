package com.fastthinkerstudios.lastminutegenius.util

import com.fastthinkerstudios.lastminutegenius.data.local.entity.CategoryEntity
import com.fastthinkerstudios.lastminutegenius.data.local.entity.VideoEntity
import com.fastthinkerstudios.lastminutegenius.domain.model.Category
import com.fastthinkerstudios.lastminutegenius.domain.model.Video

//Category
fun CategoryEntity.toDomain() = Category(id, parentId, name)
fun Category.toEntity() = CategoryEntity(id, parentId, name)

//Video
fun VideoEntity.toDomain() = Video(id, snapshot, name, categoryId, isProgressing, summary, uri)
fun Video.toEntity() = VideoEntity(id, snapshot, name, categoryId, isProgressing, summary, uri)