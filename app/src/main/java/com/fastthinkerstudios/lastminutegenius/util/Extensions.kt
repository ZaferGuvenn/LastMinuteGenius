package com.fastthinkerstudios.lastminutegenius.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import com.fastthinkerstudios.lastminutegenius.data.local.entity.CategoryEntity
import com.fastthinkerstudios.lastminutegenius.data.local.entity.VideoEntity
import com.fastthinkerstudios.lastminutegenius.domain.model.Category
import com.fastthinkerstudios.lastminutegenius.domain.model.Video
import java.io.ByteArrayOutputStream

//Category
fun CategoryEntity.toDomain() = Category(id, parentId, name)
fun Category.toEntity() = CategoryEntity(id, parentId, name)

//Video
fun VideoEntity.toDomain() = Video(id, snapshots, name, categoryId, isProgressing, summary, uri)
fun Video.toEntity() = VideoEntity(id, snapshots, name, categoryId, isProgressing, summary, uri)


// Entity için önce Bitmap'i String'e çeviren ve tersini yapan extension'lar
// Room db içerisine video snapshootları kaydetme işlemi için
fun Bitmap.toBase64(): String {
    val byteArrayOutputStream = ByteArrayOutputStream()
    this.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
    val byteArray = byteArrayOutputStream.toByteArray()
    return Base64.encodeToString(byteArray, Base64.DEFAULT)
}

fun String.toBitmap(): Bitmap? {
    return try {
        val byteArray = Base64.decode(this, Base64.DEFAULT)
        BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    } catch (e: Exception) {
        null
    }
}