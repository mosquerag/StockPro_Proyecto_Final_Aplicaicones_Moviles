package com.example.data.remote.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ApiErrorResponseDto(
    val codigo: String? = null,
    val mensaje: String? = null,
    val timestamp: String? = null,
    val error: String? = null,
    val status: Int? = null,
    val detalles: Map<String, String>? = null
) {
    fun getDescriptiveMessage(): String {
        if (!detalles.isNullOrEmpty()) {
            return detalles.values.joinToString(". ")
        }
        if (!mensaje.isNullOrBlank()) return mensaje
        if (!error.isNullOrBlank()) return error
        if (!codigo.isNullOrBlank()) return codigo
        return ""
    }
}
