package com.example.data.remote.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TasaCambioDto(
    val id: String? = null,
    val monedaOrigen: String,
    val monedaDestino: String,
    val tasa: Double,
    val fechaActualizacion: String? = null,
    val fuente: String? = null,
    val esManual: Boolean = false
)

@JsonClass(generateAdapter = true)
data class ManualTasaCambioRequestDto(
    val monedaOrigen: String,
    val monedaDestino: String,
    val tasa: Double
)
