package com.example.data.repository

import com.example.data.remote.ApiClient
import com.example.data.remote.api.StockProApi
import com.example.data.remote.dto.CategoriaDto
import com.example.data.remote.dto.CreateCategoriaRequestDto

class CategoryRepository(private val api: StockProApi) {

    suspend fun getCategories(): Result<List<CategoriaDto>> {
        return try {
            val response = api.getCategories()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(ApiClient.parseError(response)))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createCategory(request: CreateCategoriaRequestDto): Result<CategoriaDto> {
        return try {
            val response = api.createCategory(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(ApiClient.parseError(response)))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateCategory(id: String, request: CreateCategoriaRequestDto): Result<CategoriaDto> {
        return try {
            val response = api.updateCategory(id, request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(ApiClient.parseError(response)))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteCategory(id: String): Result<Unit> {
        return try {
            val response = api.deleteCategory(id)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(ApiClient.parseError(response)))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
