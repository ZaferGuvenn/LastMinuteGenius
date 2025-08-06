package com.fastthinkerstudios.lastminutegenius.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import com.fastthinkerstudios.lastminutegenius.data.local.entity.CategoryEntity
import com.fastthinkerstudios.lastminutegenius.data.local.entity.VideoEntity
import com.fastthinkerstudios.lastminutegenius.domain.model.Category
import com.fastthinkerstudios.lastminutegenius.domain.model.Video
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import androidx.core.graphics.scale

//Category
fun CategoryEntity.toDomain() = Category(id, parentId, name)
fun Category.toEntity() = CategoryEntity(id, parentId, name)

//Video
fun VideoEntity.toDomain() = Video(id, snapshots, name, categoryId, isProgressing, summary, uri)
fun Video.toEntity() = VideoEntity(id, snapshots, name, categoryId, isProgressing, summary, uri)


// bitmapin boyutunu küçültüp ideal boyuta ayarlayalım.
fun Bitmap.resize(maxSize: Int = 480): Bitmap {
    val ratio = width.toFloat() / height
    val newWidth = if (ratio > 1) maxSize else (maxSize * ratio).toInt()
    val newHeight = if (ratio > 1) (maxSize / ratio).toInt() else maxSize
    return this.scale(newWidth, newHeight)
}

// Entity için önce Bitmap'i String'e çeviren ve tersini yapan extension'lar
// Room db içerisine video snapshootları kaydetme işlemi için
fun Bitmap.toBase64(): String {
    val byteArrayOutputStream = ByteArrayOutputStream()
    this.compress(Bitmap.CompressFormat.PNG, 70, byteArrayOutputStream)
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

fun String.fromBase64ToBitmap(): Bitmap? {
    return try {
        val decodedBytes = Base64.decode(this, Base64.DEFAULT)
        BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    } catch (e: Exception) {
        null
    }
}

fun Bitmap.toTempFile(context: Context): File {
    val file = File.createTempFile("frame_", ".jpg", context.cacheDir)
    FileOutputStream(file).use { out ->
        compress(Bitmap.CompressFormat.JPEG, 90, out)
    }
    return file
}