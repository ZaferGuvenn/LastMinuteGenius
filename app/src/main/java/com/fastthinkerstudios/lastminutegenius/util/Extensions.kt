package com.fastthinkerstudios.lastminutegenius.util

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import com.fastthinkerstudios.lastminutegenius.data.local.entity.CategoryEntity
import com.fastthinkerstudios.lastminutegenius.data.local.entity.VideoEntity
import com.fastthinkerstudios.lastminutegenius.domain.model.Category
import com.fastthinkerstudios.lastminutegenius.domain.model.Video

//Category
fun CategoryEntity.toDomain() = Category(id, parentId, name)
fun Category.toEntity() = CategoryEntity(id, parentId, name)

//Video
fun VideoEntity.toDomain() = Video(id, snapshot, name, categoryId, isProgressing, summary)
fun Video.toEntity() = VideoEntity(id, snapshot, name, categoryId, isProgressing, summary)