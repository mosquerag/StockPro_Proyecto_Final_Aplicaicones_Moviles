package com.example.data.repository

import com.example.data.local.security.TokenManager
import com.example.data.remote.ApiClient
import com.example.data.remote.api.StockProApi
import com.example.data.remote.dto.BusinessDetailsDto
import com.example.data.remote.dto.TarifaImpuestoDto
import com.example.data.remote.dto.UpdateBusinessRequestDto

class BusinessRepository(
    private val api: StockProApi,
    private val tokenManager: TokenManager
) {

    suspend fun getBusiness(): Result<BusinessDetailsDto> {
        return try {
            val response = api.getBusiness()
            if (response.isSuccessful && response.body() != null) {
                val business = response.body()!!
                // Mantener sincronizado el TokenManager con el negocio activo
                tokenManager.setBusinessCurrency(business.monedaBase)
                Result.success(business)
            } else {
                val errorCode = ApiClient.parseError(response)
                Result.failure(Exception(errorCode))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateBusiness(request: UpdateBusinessRequestDto): Result<BusinessDetailsDto> {
        return try {
            val response = api.updateBusiness(request)
            if (response.isSuccessful && response.body() != null) {
                val business = response.body()!!
                tokenManager.setBusinessCurrency(business.monedaBase)
                Result.success(business)
            } else {
                val errorCode = ApiClient.parseError(response)
                Result.failure(Exception(errorCode))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getTaxRates(country: String? = null): Result<List<TarifaImpuestoDto>> {
        return try {
            val response = api.getTaxRates(country)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorCode = ApiClient.parseError(response)
                Result.failure(Exception(errorCode))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
