package com.fastthinkerstudios.lastminutegenius.presentation.category

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fastthinkerstudios.lastminutegenius.domain.model.Category
import com.fastthinkerstudios.lastminutegenius.domain.model.Video
import com.fastthinkerstudios.lastminutegenius.domain.usecase.category.DeleteCategoryUseCase
import com.fastthinkerstudios.lastminutegenius.domain.usecase.category.GetAllCategoriesUseCase
import com.fastthinkerstudios.lastminutegenius.domain.usecase.category.InsertCategoryUseCase
import com.fastthinkerstudios.lastminutegenius.domain.usecase.video.DeleteVideoUseCase
import com.fastthinkerstudios.lastminutegenius.domain.usecase.video.GetVideosByCategoryUseCase
import com.fastthinkerstudios.lastminutegenius.domain.usecase.video.InsertVideoUseCase
import com.fastthinkerstudios.lastminutegenius.domain.usecase.video.UpdateVideoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val getAllCategoriesUseCase: GetAllCategoriesUseCase,
    private val insertCategoryUseCase: InsertCategoryUseCase,
    private val deleteCategoryUseCase: DeleteCategoryUseCase,
    private val insertVideoUseCase: InsertVideoUseCase,
    private val deleteVideoUseCase: DeleteVideoUseCase,
    private val getVideosByCategoryUseCase: GetVideosByCategoryUseCase,
    private val updateVideoUseCase: UpdateVideoUseCase
): ViewModel() {

    private val _categories= MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private val _selectedVideos = MutableStateFlow<List<Uri>>(emptyList())
    val selectedVideos: StateFlow<List<Uri>> = _selectedVideos


    init {
        observeCategories()
    }

    fun addVideosToCategory(uris: List<Uri>, categoryId: Int, context: Context) {
        viewModelScope.launch {
            uris.forEach { uri ->
                val name = getFileNameFromUri(context, uri)
                val video = Video(
                    uri = uri.toString(),
                    name = name,
                    categoryId = categoryId,
                    snapshot = ""
                )
                insertVideoUseCase(video)
            }
        }
    }
    private fun getFileNameFromUri(context: Context, uri: Uri): String {
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        val nameIndex = cursor?.getColumnIndex(OpenableColumns.DISPLAY_NAME) ?: -1
        var name = "video.mp4"
        if (cursor != null && nameIndex != -1 && cursor.moveToFirst()) {
            name = cursor.getString(nameIndex)
        }
        cursor?.close()
        return name
    }

    fun onVideosSelected(uris: List<Uri>) {
        _selectedVideos.value = uris

        // Daha fazla işlem (Room'a ekleme, özet çıkarma, vb.)
    }

    private fun observeCategories(){
        viewModelScope.launch {
            getAllCategoriesUseCase()
                .onEach {
                    _categories.value = it
                    _isLoading.value = false
                }
                .catch { e ->
                    _error.value = e.message
                    _isLoading.value = false
                }
                .launchIn(this)
        }
    }

    fun addCategory(name: String, parentId:Int? = null){
        viewModelScope.launch {
            val category = Category(id = 0, parentId = parentId, name = name)
            insertCategoryUseCase(category)
        }
    }

    fun deleteCategory(category: Category){
        viewModelScope.launch {
            deleteCategoryUseCase(category)
        }
    }


}