package com.example.data.local.security

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.example.data.remote.dto.AuthResponseDto

class TokenManager(context: Context) {

    private val prefs: SharedPreferences = try {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        EncryptedSharedPreferences.create(
            context,
            PREFS_FILENAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    } catch (e: Exception) {
        // Fallback para entornos de pruebas o emuladores donde el Android KeyStore no esté disponible
        context.getSharedPreferences(PREFS_FILENAME, Context.MODE_PRIVATE)
    }

    fun saveAuthResponse(response: AuthResponseDto) {
        prefs.edit().apply {
            putString(KEY_ACCESS_TOKEN, response.accessToken)
            putString(KEY_REFRESH_TOKEN, response.refreshToken)
            putString(KEY_USER_ID, response.usuarioId)
            putString(KEY_USER_NAME, response.nombre)
            putString(KEY_USER_EMAIL, response.correo)
            response.negocioActivo?.let { negocio ->
                putString(KEY_BUSINESS_ID, negocio.id)
                putString(KEY_BUSINESS_NAME, negocio.nombre)
                putString(KEY_BUSINESS_TYPE, negocio.tipoNegocio)
                putString(KEY_BUSINESS_COUNTRY, negocio.pais)
                putString(KEY_BUSINESS_CURRENCY, negocio.monedaBase)
                putString(KEY_USER_ROLE, negocio.rol)
            }
            apply()
        }
    }

    fun updateTokens(accessToken: String, refreshToken: String?) {
        prefs.edit().apply {
            putString(KEY_ACCESS_TOKEN, accessToken)
            if (!refreshToken.isNullOrBlank()) {
                putString(KEY_REFRESH_TOKEN, refreshToken)
            }
            apply()
        }
    }

    fun getAccessToken(): String? = prefs.getString(KEY_ACCESS_TOKEN, null)

    fun getRefreshToken(): String? = prefs.getString(KEY_REFRESH_TOKEN, null)

    fun getUserId(): String? = prefs.getString(KEY_USER_ID, null)

    fun getUserName(): String? = prefs.getString(KEY_USER_NAME, null)

    fun getUserEmail(): String? = prefs.getString(KEY_USER_EMAIL, null)

    fun getBusinessId(): String? = prefs.getString(KEY_BUSINESS_ID, null)

    fun getBusinessName(): String? = prefs.getString(KEY_BUSINESS_NAME, null)

    fun getBusinessCurrency(): String = prefs.getString(KEY_BUSINESS_CURRENCY, "COP") ?: "COP"

    fun setBusinessCurrency(currency: String) {
        prefs.edit().putString(KEY_BUSINESS_CURRENCY, currency).apply()
    }

    fun getUserRole(): String? = prefs.getString(KEY_USER_ROLE, null)

    fun isLoggedIn(): Boolean = !getAccessToken().isNullOrBlank()

    fun clear() {
        prefs.edit().clear().apply()
    }

    companion object {
        private const val PREFS_FILENAME = "stockpro_secure_prefs"
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_BUSINESS_ID = "business_id"
        private const val KEY_BUSINESS_NAME = "business_name"
        private const val KEY_BUSINESS_TYPE = "business_type"
        private const val KEY_BUSINESS_COUNTRY = "business_country"
        private const val KEY_BUSINESS_CURRENCY = "business_currency"
        private const val KEY_USER_ROLE = "user_role"
    }
}
