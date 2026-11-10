package com.example.ui.util

import android.os.Build
import java.math.BigDecimal
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Currency
import java.util.Date
import java.util.Locale

object Formatters {

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
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(locale)
            date.format(formatter)
        } else {
            date.toString()
        }
    }

    fun formatDate(dateString: String?, locale: Locale = Locale.getDefault()): String {
        if (dateString.isNullOrBlank()) return ""
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                val ld = LocalDate.parse(dateString.take(10))
                val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", locale)
                ld.format(formatter)
            } catch (_: Exception) {
                dateString
            }
        } else {
            dateString
        }
    }

    fun formatDateTime(dateTime: OffsetDateTime?, locale: Locale = Locale.getDefault()): String {
        if (dateTime == null) return ""
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy · hh:mm a", locale)
            dateTime.format(formatter)
        } else {
            dateTime.toString()
        }
    }

    fun formatDateTime(dateTimeString: String?, locale: Locale = Locale.getDefault()): String {
        if (dateTimeString.isNullOrBlank()) return ""
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                val odt = OffsetDateTime.parse(dateTimeString)
                val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy · hh:mm a", locale)
                odt.format(formatter)
            } catch (_: Exception) {
                try {
                    val ldt = LocalDateTime.parse(dateTimeString)
                    val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy · hh:mm a", locale)
                    ldt.format(formatter)
                } catch (_: Exception) {
                    dateTimeString
                }
            }
        } else {
            dateTimeString
        }
    }
}
