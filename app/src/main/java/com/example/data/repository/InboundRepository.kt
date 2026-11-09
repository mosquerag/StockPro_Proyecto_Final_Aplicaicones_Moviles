package com.example.data.repository

import com.example.data.remote.ApiClient
import com.example.data.remote.api.StockProApi
import com.example.data.remote.dto.CreateEntradaPrevistaRequestDto
import com.example.data.remote.dto.EntradaPrevistaDto
import com.example.data.remote.dto.ReceiveEntradaRequestDto

class InboundRepository(private val api: StockProApi) {

    suspend fun createInboundDelivery(request: CreateEntradaPrevistaRequestDto): Result<EntradaPrevistaDto> {
        return try {
            val response = api.createInboundDelivery(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(ApiClient.parseError(response)))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getInboundToday(): Result<List<EntradaPrevistaDto>> {
        return try {
            val response = api.getInboundToday()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(ApiClient.parseError(response)))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getInboundTomorrow(): Result<List<EntradaPrevistaDto>> {
        return try {
            val response = api.getInboundTomorrow()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(ApiClient.parseError(response)))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getInboundById(id: String): Result<EntradaPrevistaDto> {
        return try {
            val response = api.getInboundById(id)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(ApiClient.parseError(response)))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun receiveInbound(id: String, request: ReceiveEntradaRequestDto? = null): Result<EntradaPrevistaDto> {
        return try {
            val response = api.receiveInbound(id, request)
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
