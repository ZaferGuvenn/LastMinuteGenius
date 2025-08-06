package com.fastthinkerstudios.lastminutegenius.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.fastthinkerstudios.lastminutegenius.data.local.entity.CategoryEntity
import com.fastthinkerstudios.lastminutegenius.data.local.entity.VideoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VideoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVideo(video: VideoEntity): Long

    @Query("SELECT * FROM videos WHERE categoryId= :categoryId")
    fun getVideosByCategory(categoryId:Int): Flow<List<VideoEntity>>

    @Update
    suspend fun updateVideo(video: VideoEntity)

    @Delete
    suspend fun deleteVideo(video: VideoEntity)

    @Query("SELECT summary FROM videos WHERE id= :videoId")
    fun getSummaryByVideoId(videoId:Int): Flow<String?>

    @Query("UPDATE videos SET summary = :newSummary WHERE id = :videoId")
    suspend fun updateSummaryByVideoId(videoId: Int, newSummary: String)
}