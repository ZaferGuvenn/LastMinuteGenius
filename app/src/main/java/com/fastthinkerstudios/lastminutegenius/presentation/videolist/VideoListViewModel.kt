package com.fastthinkerstudios.lastminutegenius.presentation.videolist

import android.app.Application
import android.graphics.Bitmap
import androidx.benchmark.perfetto.UiState
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fastthinkerstudios.lastminutegenius.data.processor.VideoProcessor
import com.fastthinkerstudios.lastminutegenius.data.repository.SummaryRepositoryImpl
import com.fastthinkerstudios.lastminutegenius.domain.model.Video
import com.fastthinkerstudios.lastminutegenius.domain.usecase.video.DeleteVideoUseCase
import com.fastthinkerstudios.lastminutegenius.domain.usecase.video.GetVideosByCategoryUseCase
import com.fastthinkerstudios.lastminutegenius.domain.usecase.video.UpdateVideoUseCase
import com.fastthinkerstudios.lastminutegenius.util.fromBase64ToBitmap
import com.fastthinkerstudios.lastminutegenius.util.toBase64
import com.fastthinkerstudios.lastminutegenius.util.toTempFile
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@HiltViewModel
class VideoListViewModel @Inject constructor(

    private val getVideosByCategoryUseCase: GetVideosByCategoryUseCase,
    private val deleteVideoUseCase: DeleteVideoUseCase,
    private val updateVideoUseCase: UpdateVideoUseCase,
    private val videoProcessor: VideoProcessor,
    private val application: Application,
    private val summaryRepo: SummaryRepositoryImpl

    ): ViewModel() {

    private val _videos = MutableStateFlow<List<Video>>(emptyList())
    val videos : StateFlow<List<Video>> = _videos.asStateFlow()


    // her video için ayrı UI state
    private val _videoStates = MutableStateFlow<Map<Int, UiState>>(emptyMap())
    val videoStates: StateFlow<Map<Int,UiState>> = _videoStates.asStateFlow()

    private val _enhancedSummary = MutableStateFlow(false)
    val enhancedSummary: StateFlow<Boolean> = _enhancedSummary

    private var languageCodeStr = "tr-TR"

    fun loadVideos(categoryId: Int){
        viewModelScope.launch {
            getVideosByCategoryUseCase(categoryId).collect{ videoList->
                _videos.value = videoList
            }
        }
    }

    fun deleteVideo(video: Video){
        viewModelScope.launch {
            deleteVideoUseCase(video)
            loadVideos(video.categoryId)
        }
    }

    fun onFramesSelected(video: Video, frames: List<Bitmap>) {
        viewModelScope.launch {
            val updatedVideo = video.copy(
                snapshots = frames.map { it.toBase64() }
            )
            updateVideoUseCase(updatedVideo)
            loadVideos(video.categoryId) // Listeyi güncelle
        }
    }


    ///özeti backendden alma işlemleri
    fun summarizeVideo(video: Video) {
        viewModelScope.launch {
            println("özetleniyor..")
            _videoStates.update { it + (video.id to UiState(isLoading = true)) }

            try {
                println("videoişlemleri0")

                // 1. IO thread’de işlemler
                val (audioFile, frameFiles) = withContext(Dispatchers.IO) {
                    val audio = videoProcessor.extractAudioFile(application, video.uri.toUri())

                    val frames = if (video.snapshots.isNotEmpty()) {
                        video.snapshots.mapNotNull { base64 ->
                            base64.fromBase64ToBitmap()?.toTempFile(application)
                        }
                    } else null

                    audio to frames
                }

                println("videoişlemleri2")

                // 2. IO: API'ye gönder
                val summary = withContext(Dispatchers.IO) {
                    summaryRepo.uploadAudioForSummary(
                        audioFile = audioFile,
                        languageCodeStr = "tr-TR",
                        frames = frameFiles
                    )
                }

                println("videoişlemleri3")

                // 3. Room’a kaydet
                val updatedVideo = video.copy(summary = summary)
                withContext(Dispatchers.IO) {
                    updateVideoUseCase(updatedVideo)
                }

                println("room işlemi tamam.")

                // 4. UI güncelle
                _videoStates.update {
                    it + (video.id to UiState(isLoading = false, summary = summary))
                }

                println("updatedVideo")
                println(updatedVideo)

                loadVideos(video.categoryId)

            } catch (e: Exception) {
                e.printStackTrace()

                val errorMessage = buildString {
                    append("Hata oluştu: ${e.message}")
                    if (e.cause != null) append("\nNeden: ${e.cause}")
                }

                _videoStates.update {
                    it + (video.id to UiState(isLoading = false, summary = errorMessage))
                }

                println("summary hatası: $errorMessage")
            }
        }
    }
}