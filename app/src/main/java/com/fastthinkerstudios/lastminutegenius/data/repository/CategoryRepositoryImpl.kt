package com.fastthinkerstudios.lastminutegenius.data.repository

import com.fastthinkerstudios.lastminutegenius.data.local.dao.CategoryDao
import com.fastthinkerstudios.lastminutegenius.domain.model.Category
import com.fastthinkerstudios.lastminutegenius.domain.repository.CategoryRepository
import com.fastthinkerstudios.lastminutegenius.util.toDomain
import com.fastthinkerstudios.lastminutegenius.util.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CategoryRepositoryImpl(
    private val dao: CategoryDao
): CategoryRepository {
    override suspend fun insertCategory(category: Category): Long {
        return dao.insertCategory(category.toEntity())
    }

    override suspend fun deleteCategory(category: Category) {
        dao.deleteCategory(category.toEntity())
    }

    override fun getAllCategories(): Flow<List<Category>> {
        return dao.getAllCategories().map { list -> list.map { it.toDomain() } }
    }
}