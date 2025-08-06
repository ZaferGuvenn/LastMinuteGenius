package com.fastthinkerstudios.lastminutegenius.presentation.summary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fastthinkerstudios.lastminutegenius.domain.usecase.summary.GetVideoSummaryByVideoIdUseCase
import com.fastthinkerstudios.lastminutegenius.domain.usecase.summary.UpdateVideoSummaryByVideoIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class SummaryViewModel @Inject constructor(

    private val getVideoSummaryByVideoIdUseCase: GetVideoSummaryByVideoIdUseCase,
    private val updateVideoSummaryByVideoIdUseCase: UpdateVideoSummaryByVideoIdUseCase

    ): ViewModel() {

    private val _summary = MutableStateFlow<String>("")
    val summary : StateFlow<String> = _summary.asStateFlow()


    fun loadVideoSummary(videoId: Int){
        viewModelScope.launch {
            getVideoSummaryByVideoIdUseCase(videoId).collect { videoSummary->
                _summary.value = videoSummary ?: "Özet çıkarılamadı!"
            }
        }
    }

    fun updateVideoSummary(videoId:Int, newSummary:String){
        viewModelScope.launch {
            updateVideoSummaryByVideoIdUseCase(videoId, newSummary)
        }
    }

}