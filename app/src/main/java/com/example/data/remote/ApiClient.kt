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

    // URL base por defecto para el emulador Android hacia el backend Spring Boot
    // private const val DEFAULT_BASE_URL = "http://10.0.2.2:8080/"
    const val DEFAULT_BASE_URL = "http://localhost:8080/"
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
     * o el mensaje descriptivo exacto enviado por el backend de Spring Boot.
     */
    fun parseError(response: Response<*>): String {
        val code = response.code()
        val errorBody = try {
            response.errorBody()?.string()
        } catch (e: Exception) {
            null
        }

        if (!errorBody.isNullOrBlank()) {
            try {
                val adapter = moshiInstance.adapter(ApiErrorResponseDto::class.java)
                val errorDto = adapter.fromJson(errorBody)
                if (errorDto != null) {
                    val descriptiveMsg = errorDto.getDescriptiveMessage()
                    if (descriptiveMsg.isNotBlank()) {
                        return descriptiveMsg
                    }
                }
            } catch (_: Exception) {
                try {
                    @Suppress("UNCHECKED_CAST")
                    val jsonMapAdapter = moshiInstance.adapter(Map::class.java)
                    val map = jsonMapAdapter.fromJson(errorBody) as? Map<String, Any?>
                    val msg = map?.get("error")?.toString() ?: map?.get("mensaje")?.toString()
                    if (!msg.isNullOrBlank()) return msg
                } catch (_: Exception) {}
            }
        }

        return when (code) {
            401 -> "CREDENCIALES_INVALIDAS"
            400 -> "VALIDACION_FALLIDA"
            404 -> "RECURSO_NO_ENCONTRADO"
            409 -> "EMAIL_DUPLICADO"
            500 -> "ERROR_INTERNO"
            else -> "HTTP_$code"
        }
    }
}
