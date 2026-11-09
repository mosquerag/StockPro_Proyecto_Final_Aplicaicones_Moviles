package com.example.data.repository

import com.example.data.remote.ApiClient
import com.example.data.remote.api.StockProApi
import com.example.data.remote.dto.ReporteGeneralDto

class ReportRepository(private val api: StockProApi) {

    suspend fun getReport(period: String = "30D", currency: String? = null): Result<ReporteGeneralDto> {
        return try {
            val response = api.getReport(period, currency)
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
