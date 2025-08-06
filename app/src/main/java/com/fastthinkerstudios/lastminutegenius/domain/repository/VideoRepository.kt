package com.fastthinkerstudios.lastminutegenius.domain.repository

import com.fastthinkerstudios.lastminutegenius.domain.model.Video
import kotlinx.coroutines.flow.Flow

interface VideoRepository {

    suspend fun insertVideo(video: Video): Long
    suspend fun deleteVideo(video: Video)
    suspend fun updateVideo(video: Video)
    fun getVideosByCategory(categoryId: Int): Flow<List<Video>>

    fun getVideoSummaryByVideoId(videoId:Int): Flow<String?>
    suspend fun updateVideoSummaryByVideoId(videoId:Int, newSummary:String)
}