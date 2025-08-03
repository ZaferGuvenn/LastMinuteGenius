package com.fastthinkerstudios.lastminutegenius.presentation.main

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fastthinkerstudios.lastminutegenius.data.processor.VideoProcessor
import com.fastthinkerstudios.lastminutegenius.data.repository.SummaryRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val application: Application,
    private val videoProcessor: VideoProcessor,
    private val summaryRepo: SummaryRepositoryImpl
): ViewModel(){

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState

    private val _enhancedSummary = MutableStateFlow(false)
    val enhancedSummary: StateFlow<Boolean> = _enhancedSummary

    private var languageCodeStr = "tr-TR"

    // Video URI saklanıyor
    private val _selectedVideoUri = MutableStateFlow<Uri?>(null)
    val selectedVideoUri: StateFlow<Uri?> = _selectedVideoUri

    private val _selectedFrames = mutableStateListOf<Bitmap>()
    val selectedFrames: List<Bitmap> get() = _selectedFrames


    // VideoProcessor'ı dışarıya aç
    fun getVideoProcessor(): VideoProcessor = videoProcessor

    fun addFrame(bitmap: Bitmap) {
        if (_selectedFrames.size < 5) _selectedFrames.add(bitmap)
    }

    fun removeFrameAt(index: Int) {
        _selectedFrames.removeAt(index)
    }

    fun setSelectedVideoUri(uri: Uri) {
        _selectedVideoUri.value = uri
    }

    fun clearSelectedVideoUri() {
        _selectedVideoUri.value = null
    }

    fun setEnhancedSummary(enabled: Boolean) {
        _enhancedSummary.value = enabled
    }

    fun setLanguageCode(code: String) {
        languageCodeStr = code
    }

    fun processSelectedVideo() {
        _selectedVideoUri.value?.let {
            onVideoSelected(it)
        }
    }

    private fun onVideoSelected(uri: Uri) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(isLoading = true, summary = "")
            }

            try {
                val audioFile = videoProcessor.extractAudioFile(application, uri)

                val frames: List<File>? = if (_enhancedSummary.value) {
                    videoProcessor.extractFrames(application, uri)
                } else {
                    null
                }

                val summary = summaryRepo.uploadAudioForSummary(
                    audioFile = audioFile,
                    languageCodeStr = languageCodeStr,
                    frames = frames
                )

                _uiState.update {
                    it.copy(isLoading = false, summary = summary)
                }

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, summary = "Hata oluştu: ${e.message}")
                }
            }
        }
    }

}