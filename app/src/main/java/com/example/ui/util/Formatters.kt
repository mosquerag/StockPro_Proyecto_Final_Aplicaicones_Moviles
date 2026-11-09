package com.example.ui.util

import java.math.BigDecimal
import java.text.NumberFormat
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Currency
import java.util.Locale

object Formatters {

    /**
     * Formatea montos monetarios respetando las reglas de negocio y el Locale:
     * - COP: sin decimales (ej. $ 520.000)
     * - USD y EUR: con dos decimales (ej. $ 520.00 / € 520,00)
     */
    fun formatCurrency(amount: BigDecimal?, currencyCode: String = "COP", locale: Locale = Locale.getDefault()): String {
        val value = amount ?: BigDecimal.ZERO
        val format = NumberFormat.getCurrencyInstance(locale)
        try {
            val validCode = currencyCode.uppercase().trim()
            if (validCode.length == 3) {
                format.currency = Currency.getInstance(validCode)
            }
        } catch (_: Exception) {
            // Fallback a moneda por defecto
        }

        if (currencyCode.equals("COP", ignoreCase = true)) {
            format.maximumFractionDigits = 0
            format.minimumFractionDigits = 0
        } else {
            format.maximumFractionDigits = 2
            format.minimumFractionDigits = 2
        }
        return format.format(value)
    }

    fun formatDate(date: LocalDate?, locale: Locale = Locale.getDefault()): String {
        if (date == null) return ""
        val formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(locale)
        return date.format(formatter)
    }

    fun formatDateTime(dateTime: OffsetDateTime?, locale: Locale = Locale.getDefault()): String {
        if (dateTime == null) return ""
        val formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT).withLocale(locale)
        return dateTime.format(formatter)
    }
}
