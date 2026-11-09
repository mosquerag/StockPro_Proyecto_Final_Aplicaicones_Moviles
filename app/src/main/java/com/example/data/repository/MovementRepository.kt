package com.example.data.repository

import com.example.data.remote.ApiClient
import com.example.data.remote.api.StockProApi
import com.example.data.remote.dto.CreateMovimientoRequestDto
import com.example.data.remote.dto.MovimientoInventarioDto

class MovementRepository(private val api: StockProApi) {

    suspend fun createMovement(request: CreateMovimientoRequestDto): Result<MovimientoInventarioDto> {
        return try {
            val response = api.createMovement(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(ApiClient.parseError(response)))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getMovements(inicio: String? = null, fin: String? = null): Result<List<MovimientoInventarioDto>> {
        return try {
            val response = api.getMovements(inicio, fin)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(ApiClient.parseError(response)))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
