package com.example.data.remote.interceptor

import com.example.data.local.security.TokenManager
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val tokenManager: TokenManager) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // Si la petición ya incluye cabecera Authorization o es una ruta pública de autenticación, procedemos
        val path = originalRequest.url.encodedPath
        if (path.contains("/api/v1/auth/login") ||
            path.contains("/api/v1/auth/register") ||
            path.contains("/api/v1/auth/refresh")
        ) {
            return chain.proceed(originalRequest)
        }

        val token = tokenManager.getAccessToken()

        val requestBuilder = originalRequest.newBuilder()
        if (!token.isNullOrBlank()) {
            requestBuilder.header("Authorization", "Bearer $token")
        }

        return chain.proceed(requestBuilder.build())
    }
}
