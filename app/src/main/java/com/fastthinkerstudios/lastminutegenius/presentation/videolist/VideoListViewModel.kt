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
import com.fastthinkerstudios.lastminutegenius.util.GcsUploader
import com.fastthinkerstudios.lastminutegenius.util.fromBase64ToBitmap
import com.fastthinkerstudios.lastminutegenius.util.resize
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
                snapshots = frames.map {
                    it.resize()//bitmapi ideal boyuta ayarlayalım
                    it.toBase64()//bitmapin string türünde vt ye kaydedilmeye müsait hali

                }
            )
            updateVideoUseCase(updatedVideo)
            loadVideos(video.categoryId) // Listeyi güncelle
        }
    }


    ///özeti backendden alma işlemleri
    fun summarizeVideo(video: Video) {
        viewModelScope.launch {
            try {
                println("özetleniyor..")
                _videoStates.update { it + (video.id to UiState(isLoading = true)) }

                // 1. Ses dosyasını çıkar
                val audioFile = withContext(Dispatchers.IO) {
                    videoProcessor.extractAudioFile(application, video.uri.toUri())
                }

                // 2. Firebase Storage'a yükle ve GCS URI al
                val gcsUri = withContext(Dispatchers.IO) {
                    GcsUploader.uploadFlacToGcs(audioFile)
                        ?: throw Exception("GCS URI alınamadı")
                }

                println("gcsUri")
                println(gcsUri)

                // 3. Snapshotları temp dosyaya dönüştür
                val frameFiles = if (video.snapshots.isNotEmpty()) {
                    video.snapshots.mapNotNull { base64 ->
                        base64.fromBase64ToBitmap()?.toTempFile(application)
                    }
                } else null

                // 4. API'ye GCS URI ile istek gönder
                val summary = withContext(Dispatchers.IO) {
                    summaryRepo.uploadAudioUriForSummary(
                        gcsUri = gcsUri,
                        languageCode = "tr-TR",
                        frames = frameFiles
                    )
                }

                // 5. Room güncelle ve UI bildir
                val updatedVideo = video.copy(summary = summary)
                withContext(Dispatchers.IO) {
                    updateVideoUseCase(updatedVideo)
                }
                _videoStates.update { it + (video.id to UiState(isLoading = false, summary = summary)) }

                println("Özetleme tamamlandı: $summary")

            } catch (e: Exception) {
                e.printStackTrace()
                _videoStates.update {
                    it + (video.id to UiState(isLoading = false, summary = "Hata: ${e.message}"))
                }
                println("summary hatası: ${e.message}")
            }
        }
    }

}