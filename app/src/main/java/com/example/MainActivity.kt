package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import com.example.ui.navigation.StockProApp
import com.example.ui.theme.StockProTheme
import com.example.ui.util.LocaleManager
import com.example.ui.util.ProvideAppLocale

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            var currentLanguage by remember {
                mutableStateOf(LocaleManager.getSavedLanguage(this@MainActivity))
            }

            ProvideAppLocale(languageCode = currentLanguage) {
                StockProTheme {
                    StockProApp(
                        currentLanguage = currentLanguage,
                        onLanguageChanged = { newLang ->
                            currentLanguage = newLang
                        }
                    )
                }
            }
        }
    }
}
