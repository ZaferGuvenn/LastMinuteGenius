package com.fastthinkerstudios.lastminutegenius.domain.usecase.summary

import com.fastthinkerstudios.lastminutegenius.domain.repository.VideoRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow

class UpdateVideoSummaryByVideoIdUseCase @Inject constructor(private val repository: VideoRepository) {

    suspend operator fun invoke(videoId:Int, newSummary:String) {

        return repository.updateVideoSummaryByVideoId(videoId, newSummary)

    }
}