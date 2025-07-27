package com.fastthinkerstudios.lastminutegenius.presentation.main

import android.app.Application
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fastthinkerstudios.lastminutegenius.data.processor.VideoProcessor
import com.fastthinkerstudios.lastminutegenius.data.remote.SummaryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val application: Application,
    private val videoProcessor: VideoProcessor,
    private val summaryRepo: SummaryRepository
): ViewModel(){

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState

    fun onVideoSelected(uri: Uri){
        viewModelScope.launch {
            _uiState.update {
                it.copy(isLoading = true, summary = "")
            }

            try {

                val audioFile = videoProcessor.extractAudioFile(application, uri)

                val summary = summaryRepo.uploadAudioForSummary(audioFile, languageCodeStr = "tr-TR")

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        summary = summary
                    )
                }

            }catch (e: Exception){
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        summary = "Hata oluştu: ${e.message}"
                    )
                }
            }

        }
    }



}