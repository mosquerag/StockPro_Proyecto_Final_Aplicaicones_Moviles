package com.example.data.repository

import com.example.data.remote.ApiClient
import com.example.data.remote.api.StockProApi
import com.example.data.remote.dto.BajaLoteRequestDto
import com.example.data.remote.dto.CreateLoteRequestDto
import com.example.data.remote.dto.LoteDto
import com.example.data.remote.dto.VencimientoAlertDto

class LotRepository(private val api: StockProApi) {

    suspend fun getLotsByProduct(productoId: String): Result<List<LoteDto>> {
        return try {
            val response = api.getLotsByProduct(productoId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(ApiClient.parseError(response)))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createLot(request: CreateLoteRequestDto): Result<LoteDto> {
        return try {
            val response = api.createLot(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(ApiClient.parseError(response)))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun darDeBajaLote(id: String, motivo: String): Result<Unit> {
        return try {
            val response = api.darDeBajaLote(id, BajaLoteRequestDto(motivo))
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(ApiClient.parseError(response)))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getExpiringLots(): Result<List<VencimientoAlertDto>> {
        return try {
            val response = api.getExpiringLots()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(ApiClient.parseError(response)))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getExpiredLots(): Result<List<VencimientoAlertDto>> {
        return try {
            val response = api.getExpiredLots()
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
