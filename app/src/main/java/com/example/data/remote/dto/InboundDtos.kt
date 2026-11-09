package com.example.data.remote.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CreateDetalleEntradaRequestDto(
    val productoId: String,
    val cantidadEsperada: Double,
    val costoUnitario: Double? = null
)

@JsonClass(generateAdapter = true)
data class CreateEntradaPrevistaRequestDto(
    val fechaPrevista: String, // YYYY-MM-DD
    val origen: String? = null,
    val notas: String? = null,
    val items: List<CreateDetalleEntradaRequestDto>
)

@JsonClass(generateAdapter = true)
data class ReceiveItemRequestDto(
    val productoId: String,
    val cantidadRecibida: Double,
    val fechaVencimiento: String? = null,
    val numeroLote: String? = null
)

@JsonClass(generateAdapter = true)
data class ReceiveEntradaRequestDto(
    val items: List<ReceiveItemRequestDto>? = null,
    val notas: String? = null
)

@JsonClass(generateAdapter = true)
data class DetalleEntradaPrevistaDto(
    val id: String,
    val productoId: String,
    val productoNombre: String? = null,
    val cantidadEsperada: Double,
    val cantidadRecibida: Double = 0.0,
    val costoUnitario: Double = 0.0,
    val estado: String = "PENDIENTE",
    val loteId: String? = null,
    val numeroLote: String? = null
)

@JsonClass(generateAdapter = true)
data class EntradaPrevistaDto(
    val id: String,
    val negocioId: String? = null,
    val numeroOrden: String,
    val fechaPrevista: String,
    val estado: String = "PENDIENTE",
    val totalEstimado: Double = 0.0,
    val notas: String? = null,
    val fechaRecibida: String? = null,
    val createdAt: String? = null,
    val items: List<DetalleEntradaPrevistaDto> = emptyList()
)
