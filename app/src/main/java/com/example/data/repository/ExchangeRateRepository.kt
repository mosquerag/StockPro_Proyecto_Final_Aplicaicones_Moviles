package com.example.data.repository

import com.example.data.remote.ApiClient
import com.example.data.remote.api.StockProApi
import com.example.data.remote.dto.ManualTasaCambioRequestDto
import com.example.data.remote.dto.TasaCambioDto

class ExchangeRateRepository(private val api: StockProApi) {

    suspend fun getExchangeRates(): Result<List<TasaCambioDto>> {
        return try {
            val response = api.getExchangeRates()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(ApiClient.parseError(response)))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun refreshExchangeRates(): Result<List<TasaCambioDto>> {
        return try {
            val response = api.refreshExchangeRates()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(ApiClient.parseError(response)))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun setManualExchangeRate(request: ManualTasaCambioRequestDto): Result<TasaCambioDto> {
        return try {
            val response = api.setManualExchangeRate(request)
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
