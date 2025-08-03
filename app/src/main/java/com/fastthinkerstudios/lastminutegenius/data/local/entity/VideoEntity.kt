package com.fastthinkerstudios.lastminutegenius.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("videos")
data class VideoEntity(

    @PrimaryKey(autoGenerate = true)
    val id:Int=0,
    val snapshot: String,            // galeriden gelen uri (path)
    val name: String,                // video dosya adı
    val categoryId: Int,             // kategoriye ait foreign key
    val isProgressing: Boolean = false,  // özetleme işlemi devam ediyor mu
    val summary: String? = null,     // backend'den gelen özet
    val uri: String
)