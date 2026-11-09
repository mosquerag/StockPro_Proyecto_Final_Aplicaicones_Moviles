package com.example.data.repository

import com.example.data.remote.ApiClient
import com.example.data.remote.api.StockProApi
import com.example.data.remote.dto.CreateVentaRequestDto
import com.example.data.remote.dto.VentaDto

class SaleRepository(private val api: StockProApi) {

    suspend fun createSale(request: CreateVentaRequestDto): Result<VentaDto> {
        return try {
            val response = api.createSale(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(ApiClient.parseError(response)))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getSales(inicio: String? = null, fin: String? = null): Result<List<VentaDto>> {
        return try {
            val response = api.getSales(inicio, fin)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(ApiClient.parseError(response)))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getSaleById(id: String): Result<VentaDto> {
        return try {
            val response = api.getSaleById(id)
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
