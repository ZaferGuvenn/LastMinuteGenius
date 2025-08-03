package com.fastthinkerstudios.lastminutegenius.domain.usecase.category

import com.fastthinkerstudios.lastminutegenius.domain.model.Category
import com.fastthinkerstudios.lastminutegenius.domain.repository.CategoryRepository
import javax.inject.Inject

class InsertCategoryUseCase @Inject constructor(private val repository: CategoryRepository) {

    suspend operator fun invoke(category: Category): Long{
        return repository.insertCategory(category)
    }
}