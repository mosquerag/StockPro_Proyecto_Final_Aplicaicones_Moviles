package com.example.data.remote.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ApiErrorResponseDto(
    val codigo: String? = null,
    val mensaje: String? = null,
    val timestamp: String? = null,
    val error: String? = null
)
