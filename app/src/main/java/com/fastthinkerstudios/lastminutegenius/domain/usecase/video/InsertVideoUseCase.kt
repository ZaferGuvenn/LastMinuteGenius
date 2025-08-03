package com.fastthinkerstudios.lastminutegenius.domain.usecase.video

import com.fastthinkerstudios.lastminutegenius.domain.model.Video
import com.fastthinkerstudios.lastminutegenius.domain.repository.VideoRepository

class InsertVideoUseCase(private val repository: VideoRepository) {

    suspend operator fun invoke(video: Video):Long{
        return repository.insertVideo(video)
    }
}