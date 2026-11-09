package com.example.data.remote.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CreateMovimientoRequestDto(
    val productoId: String,
    val loteId: String? = null,
    val tipoMovimiento: String,
    val cantidad: Double,
    val motivo: String
)

@JsonClass(generateAdapter = true)
data class MovimientoInventarioDto(
    val id: String,
    val productoId: String,
    val productoNombre: String? = null,
    val loteId: String? = null,
    val numeroLote: String? = null,
    val tipoMovimiento: String,
    val cantidad: Double,
    val stockAnterior: Double = 0.0,
    val stockPosterior: Double = 0.0,
    val costoReferencia: Double = 0.0,
    val motivo: String,
    val usuarioNombre: String? = null,
    val referenciaTipo: String? = null,
    val referenciaId: String? = null,
    val fechaMovimiento: String
)
