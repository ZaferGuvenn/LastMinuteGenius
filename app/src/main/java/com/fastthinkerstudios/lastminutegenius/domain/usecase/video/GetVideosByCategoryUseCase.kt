package com.fastthinkerstudios.lastminutegenius.domain.usecase.video

import com.fastthinkerstudios.lastminutegenius.domain.model.Video
import com.fastthinkerstudios.lastminutegenius.domain.repository.VideoRepository
import com.fastthinkerstudios.lastminutegenius.util.toDomain
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetVideosByCategoryUseCase @Inject constructor( private val repository: VideoRepository) {

    operator fun invoke(categoryId:Int): Flow<List<Video>>{

        return repository.getVideosByCategory(categoryId).map { entities ->
            entities.map {
                it
            }
        }
    }
}