package com.fastthinkerstudios.lastminutegenius.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.fastthinkerstudios.lastminutegenius.data.local.converter.StringListConverter
import com.fastthinkerstudios.lastminutegenius.data.local.dao.CategoryDao
import com.fastthinkerstudios.lastminutegenius.data.local.dao.QuizDao
import com.fastthinkerstudios.lastminutegenius.data.local.dao.VideoDao
import com.fastthinkerstudios.lastminutegenius.data.local.entity.CategoryEntity
import com.fastthinkerstudios.lastminutegenius.data.local.entity.QuizEntity
import com.fastthinkerstudios.lastminutegenius.data.local.entity.VideoEntity


@Database(
    entities = [CategoryEntity::class, VideoEntity::class, QuizEntity::class],
    version = 1,
    exportSchema = false
)

@TypeConverters(StringListConverter::class)
abstract class AppDatabase : RoomDatabase(){
    abstract fun categoryDao(): CategoryDao
    abstract fun videoDao(): VideoDao
    abstract fun quizDao(): QuizDao
}