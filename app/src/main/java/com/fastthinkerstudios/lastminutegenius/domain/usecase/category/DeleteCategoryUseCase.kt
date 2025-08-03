package com.fastthinkerstudios.lastminutegenius.domain.usecase.category

import com.fastthinkerstudios.lastminutegenius.domain.model.Category
import com.fastthinkerstudios.lastminutegenius.domain.repository.CategoryRepository

class DeleteCategoryUseCase( private val repository: CategoryRepository){

    suspend operator fun invoke(category: Category){
        repository.deleteCategory(category)
    }

}