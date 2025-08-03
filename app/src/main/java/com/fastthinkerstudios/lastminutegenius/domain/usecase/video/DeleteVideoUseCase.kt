package com.fastthinkerstudios.lastminutegenius.domain.usecase.video

import com.fastthinkerstudios.lastminutegenius.domain.model.Video
import com.fastthinkerstudios.lastminutegenius.domain.repository.VideoRepository

class DeleteVideoUseCase(private val repository: VideoRepository) {

    suspend operator fun invoke(video: Video) {
        repository.deleteVideo(video)
    }
}