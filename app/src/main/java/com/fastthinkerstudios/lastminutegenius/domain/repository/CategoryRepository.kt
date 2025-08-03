package com.fastthinkerstudios.lastminutegenius.domain.repository

import com.fastthinkerstudios.lastminutegenius.domain.model.Category
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {

    suspend fun insertCategory(category: Category): Long
    suspend fun deleteCategory(category: Category)
    fun getAllCategories(): Flow<List<Category>>
}