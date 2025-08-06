package com.fastthinkerstudios.lastminutegenius.domain.usecase.summary

import com.fastthinkerstudios.lastminutegenius.domain.repository.VideoRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow

class GetVideoSummaryByVideoIdUseCase @Inject constructor(private val repository: VideoRepository) {

    operator fun invoke(categoryId:Int): Flow<String?> {

        return repository.getVideoSummaryByVideoId(categoryId)

    }
}