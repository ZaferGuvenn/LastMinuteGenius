package com.fastthinkerstudios.lastminutegenius.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.fastthinkerstudios.lastminutegenius.data.local.dao.CategoryDao
import com.fastthinkerstudios.lastminutegenius.data.local.dao.VideoDao
import com.fastthinkerstudios.lastminutegenius.data.local.entity.CategoryEntity
import com.fastthinkerstudios.lastminutegenius.data.local.entity.VideoEntity


@Database(
    entities = [CategoryEntity::class, VideoEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase(){
    abstract fun categoryDao(): CategoryDao
    abstract fun videoDao(): VideoDao
}