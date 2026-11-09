package com.example.data.remote.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LoteDto(
    val id: String,
    val productoId: String,
    val productoNombre: String? = null,
    val negocioId: String? = null,
    val numeroLote: String,
    val cantidadInicial: Double,
    val cantidadDisponible: Double,
    val fechaIngreso: String? = null,
    val fechaVencimiento: String? = null,
    val estado: String = "ACTIVO",
    val createdAt: String? = null
)

@JsonClass(generateAdapter = true)
data class CreateLoteRequestDto(
    val productoId: String,
    val numeroLote: String,
    val cantidad: Double,
    val fechaVencimiento: String? = null,
    val motivo: String = "Ingreso de nuevo lote al inventario"
)

@JsonClass(generateAdapter = true)
data class BajaLoteRequestDto(
    val motivo: String = "Baja por fecha de vencimiento expirada"
)

@JsonClass(generateAdapter = true)
data class VencimientoAlertDto(
    val loteId: String,
    val productoId: String,
    val productoNombre: String,
    val categoriaNombre: String? = null,
    val numeroLote: String,
    val cantidadDisponible: Double,
    val fechaVencimiento: String,
    val diasRestantes: Long,
    val estadoAlerta: String
)
