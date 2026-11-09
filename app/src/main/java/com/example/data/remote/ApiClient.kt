package com.example.data.remote

import android.content.Context
import com.example.data.local.security.TokenManager
import com.example.data.remote.api.StockProApi
import com.example.data.remote.dto.ApiErrorResponseDto
import com.example.data.remote.interceptor.AuthInterceptor
import com.example.data.remote.interceptor.TokenAuthenticator
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {

    private const val DEFAULT_BASE_URL = "http://10.0.2.2:8080/"

    private var moshiInstance: Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    @Volatile
    private var apiInstance: StockProApi? = null

    fun getMoshi(): Moshi = moshiInstance

    fun getApi(context: Context): StockProApi {
        return apiInstance ?: synchronized(this) {
            val tokenManager = TokenManager(context.applicationContext)
            val loggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

            val okHttpClient = OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(AuthInterceptor(tokenManager))
                .authenticator(TokenAuthenticator(tokenManager, DEFAULT_BASE_URL))
                .addInterceptor(loggingInterceptor)
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(DEFAULT_BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(MoshiConverterFactory.create(moshiInstance))
                .build()

            val service = retrofit.create(StockProApi::class.java)
            apiInstance = service
            service
        }
    }

    /**
     * Parsea el cuerpo de error HTTP para extraer el código estandarizado (ej: STOCK_INSUFICIENTE)
     * o el mensaje de error para traducirlo con ErrorTranslator.
     */
    fun parseError(response: Response<*>): String {
        return try {
            val errorBody = response.errorBody()?.string()
            if (!errorBody.isNullOrBlank()) {
                val adapter = moshiInstance.adapter(ApiErrorResponseDto::class.java)
                val errorDto = adapter.fromJson(errorBody)
                errorDto?.codigo ?: errorDto?.mensaje ?: errorDto?.error ?: "ERROR_DESCONOCIDO"
            } else {
                "HTTP_${response.code()}"
            }
        } catch (e: Exception) {
            "HTTP_${response.code()}"
        }
    }
}
