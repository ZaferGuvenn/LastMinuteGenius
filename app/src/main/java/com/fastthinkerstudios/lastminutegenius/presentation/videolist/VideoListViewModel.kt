package com.fastthinkerstudios.lastminutegenius.presentation.videolist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fastthinkerstudios.lastminutegenius.domain.model.Video
import com.fastthinkerstudios.lastminutegenius.domain.usecase.video.GetVideosByCategoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class VideoListViewModel @Inject constructor(

    private val getVideosByCategoryUseCase: GetVideosByCategoryUseCase

): ViewModel() {

    private val _videos = MutableStateFlow<List<Video>>(emptyList())
    val videos : StateFlow<List<Video>> = _videos.asStateFlow()

    fun loadVideos(categoryId: Int){
        viewModelScope.launch {
            getVideosByCategoryUseCase(categoryId).collect{ videoList->
                _videos.value = videoList
            }
        }
    }
}