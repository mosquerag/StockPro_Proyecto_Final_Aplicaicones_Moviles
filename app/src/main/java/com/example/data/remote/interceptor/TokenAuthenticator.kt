package com.example.data.remote.interceptor

import com.example.data.local.security.TokenManager
import com.squareup.moshi.Moshi
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

class TokenAuthenticator(
    private val tokenManager: TokenManager,
    private val baseUrl: String = "http://10.0.2.2:8080/"
) : Authenticator {

    @Synchronized
    override fun authenticate(route: Route?, response: Response): Request? {
        // Si el fallo ocurrió en la propia ruta de refresh o login, no reintentar
        if (response.request.url.encodedPath.contains("/api/v1/auth/")) {
            return null
        }

        // Si ya intentamos refrescar y falló más de 2 veces, abortar
        if (responseCount(response) >= 2) {
            tokenManager.clear()
            return null
        }

        val refreshToken = tokenManager.getRefreshToken()
        if (refreshToken.isNullOrBlank()) {
            tokenManager.clear()
            return null
        }

        // Llamada síncrona directa para refrescar el token
        val refreshClient = OkHttpClient.Builder().build()
        val jsonMediaType = "application/json; charset=utf-8".toMediaType()
        val requestBody = "{\"refreshToken\":\"$refreshToken\"}".toRequestBody(jsonMediaType)

        val refreshUrl = baseUrl.trimEnd('/') + "/api/v1/auth/refresh"
        val refreshRequest = Request.Builder()
            .url(refreshUrl)
            .post(requestBody)
            .build()

        return try {
            val refreshResponse = refreshClient.newCall(refreshRequest).execute()
            if (refreshResponse.isSuccessful) {
                val bodyString = refreshResponse.body?.string() ?: ""
                val json = JSONObject(bodyString)
                val newAccessToken = json.optString("accessToken", "")
                val newRefreshToken = json.optString("refreshToken", refreshToken)

                if (newAccessToken.isNotBlank()) {
                    tokenManager.updateTokens(newAccessToken, newRefreshToken)

                    return response.request.newBuilder()
                        .header("Authorization", "Bearer $newAccessToken")
                        .build()
                } else {
                    tokenManager.clear()
                    null
                }
            } else {
                tokenManager.clear()
                null
            }
        } catch (e: Exception) {
            tokenManager.clear()
            null
        }
    }

    private fun responseCount(response: Response): Int {
        var count = 1
        var prior = response.priorResponse
        while (prior != null) {
            count++
            prior = prior.priorResponse
        }
        return count
    }
}
