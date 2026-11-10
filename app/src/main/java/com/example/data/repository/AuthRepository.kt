package com.example.data.repository

import com.example.data.local.db.StockProDatabase
import com.example.data.local.security.TokenManager
import com.example.data.remote.ApiClient
import com.example.data.remote.api.StockProApi
import com.example.data.remote.dto.AuthResponseDto
import com.example.data.remote.dto.LoginRequestDto
import com.example.data.remote.dto.RegisterBusinessRequestDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class AuthRepository(
    private val api: StockProApi,
    private val tokenManager: TokenManager,
    private val database: StockProDatabase? = null
) {

    suspend fun login(correo: String, password: String): Result<AuthResponseDto> {
        return try {
            val response = api.login(LoginRequestDto(correo, password))
            if (response.isSuccessful && response.body() != null) {
                val authResponse = response.body()!!
                tokenManager.saveAuthResponse(authResponse)
                Result.success(authResponse)
            } else {
                val errorCode = ApiClient.parseError(response)
                Result.failure(Exception(errorCode))
            }
        } catch (e: Exception) {
            val errorMsg = when (e) {
                is ConnectException,
                is SocketTimeoutException,
                is UnknownHostException,
                is IOException -> "ERROR_CONEXION"
                else -> e.message ?: "ERROR_INTERNO"
            }
            Result.failure(Exception(errorMsg))
        }
    }

    suspend fun registerBusiness(request: RegisterBusinessRequestDto): Result<AuthResponseDto> {
        return try {
            val response = api.registerBusiness(request)
            if (response.isSuccessful && response.body() != null) {
                val authResponse = response.body()!!
                tokenManager.saveAuthResponse(authResponse)
                Result.success(authResponse)
            } else {
                val errorCode = ApiClient.parseError(response)
                Result.failure(Exception(errorCode))
            }
        } catch (e: Exception) {
            val errorMsg = when (e) {
                is ConnectException,
                is SocketTimeoutException,
                is UnknownHostException,
                is IOException -> "ERROR_CONEXION"
                else -> e.message ?: "ERROR_INTERNO"
            }
            Result.failure(Exception(errorMsg))
        }
    }

    suspend fun logout(): Result<Unit> {
        try {
            api.logout()
        } catch (_: Exception) {
            // Continúa limpiando la sesión localmente aunque la llamada de red falle
        }
        withContext(Dispatchers.IO) {
            try {
                database?.clearAllTables()
            } catch (_: Exception) {
                // Silencioso si la BD ya fue limpiada
            }
        }
        tokenManager.clear()
        return Result.success(Unit)
    }

    fun isLoggedIn(): Boolean = tokenManager.isLoggedIn()

    fun getBusinessName(): String = tokenManager.getBusinessName() ?: "Mi Negocio"

    fun getBusinessCurrency(): String = tokenManager.getBusinessCurrency()

    fun getUserEmail(): String = tokenManager.getUserEmail() ?: ""

    fun getUserName(): String = tokenManager.getUserName() ?: ""

    fun getUserRole(): String = tokenManager.getUserRole() ?: "PROPIETARIO"
}
