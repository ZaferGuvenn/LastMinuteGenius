package com.fastthinkerstudios.lastminutegenius.data.repository

import com.fastthinkerstudios.lastminutegenius.data.local.dao.VideoDao
import com.fastthinkerstudios.lastminutegenius.domain.model.Video
import com.fastthinkerstudios.lastminutegenius.domain.repository.VideoRepository
import com.fastthinkerstudios.lastminutegenius.util.toDomain
import com.fastthinkerstudios.lastminutegenius.util.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class VideoRepositoryImpl(
    private val dao: VideoDao
) : VideoRepository {
    override suspend fun insertVideo(video: Video): Long {
        return dao.insertVideo(video.toEntity())
    }

    override suspend fun deleteVideo(video: Video) {
        dao.deleteVideo(video.toEntity())
    }

    override suspend fun updateVideo(video: Video) {
        dao.updateVideo(video.toEntity())
    }

    override fun getVideosByCategory(categoryId: Int): Flow<List<Video>> {
        return dao.getVideosByCategory(categoryId).map { list -> list.map { it.toDomain() } }
    }

    override fun getVideoSummaryByVideoId(videoId: Int): Flow<String?> {
        return dao.getSummaryByVideoId(videoId)
    }

    override suspend fun updateVideoSummaryByVideoId(videoId: Int, newSummary: String) {
        return dao.updateSummaryByVideoId(videoId,newSummary)
    }
}