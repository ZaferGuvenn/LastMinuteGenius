package com.fastthinkerstudios.lastminutegenius.domain.usecase.category

import com.fastthinkerstudios.lastminutegenius.domain.model.Category
import com.fastthinkerstudios.lastminutegenius.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow

class GetAllCategoriesUseCase(
    private val repository: CategoryRepository
) {
    operator fun invoke(): Flow<List<Category>>{
        return repository.getAllCategories()
    }
}