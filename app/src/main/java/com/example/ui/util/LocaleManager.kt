package com.example.ui.util

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.LocaleList
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import java.util.Locale

object LocaleManager {
    private const val PREF_NAME = "stockpro_locale_prefs"
    private const val KEY_LANGUAGE = "selected_language"

    fun getSavedLanguage(context: Context): String {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val saved = prefs.getString(KEY_LANGUAGE, null)
        if (saved != null) return saved

        // Idioma del dispositivo si es español, de lo contrario inglés
        val defaultTag = Locale.getDefault().language.lowercase()
        return if (defaultTag.startsWith("es")) "es" else "en"
    }

    fun saveLanguage(context: Context, langCode: String) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_LANGUAGE, langCode).apply()

        // Notificar al sistema en Android 13+ si está disponible
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            try {
                val localeManager = context.getSystemService(android.app.LocaleManager::class.java)
                localeManager?.applicationLocales = LocaleList.forLanguageTags(langCode)
            } catch (_: Exception) {}
        }
    }
}

val LocalAppLanguage = compositionLocalOf { "es" }

@Composable
fun ProvideAppLocale(
    languageCode: String,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val currentConfig = LocalConfiguration.current

    val locale = remember(languageCode) { Locale(languageCode) }

    val updatedConfig = remember(languageCode, currentConfig) {
        Configuration(currentConfig).apply {
            setLocale(locale)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                setLocales(LocaleList(locale))
            }
        }
    }

    val localizedContext = remember(languageCode, context) {
        context.createConfigurationContext(updatedConfig)
    }

    CompositionLocalProvider(
        LocalConfiguration provides updatedConfig,
        LocalContext provides localizedContext,
        LocalAppLanguage provides languageCode,
        content = content
    )
}
