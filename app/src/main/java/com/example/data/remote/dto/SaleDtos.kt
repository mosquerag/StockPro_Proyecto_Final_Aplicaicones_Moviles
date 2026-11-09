package com.example.data.remote.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CreateVentaItemRequestDto(
    val productoId: String,
    val cantidad: Double,
    val precioUnitario: Double? = null,
    val descuento: Double = 0.0
)

@JsonClass(generateAdapter = true)
data class CreateVentaRequestDto(
    val items: List<CreateVentaItemRequestDto>,
    val monedaVenta: String? = null,
    val tasaCambioManual: Double? = null,
    val metodoPago: String = "EFECTIVO",
    val notas: String? = null
)

@JsonClass(generateAdapter = true)
data class DetalleVentaDto(
    val id: String,
    val productoId: String,
    val productoNombre: String? = null,
    val loteId: String? = null,
    val numeroLote: String? = null,
    val cantidad: Double,
    val precioUnitario: Double,
    val costoUnitario: Double = 0.0,
    val subtotalBruto: Double,
    val descuento: Double = 0.0,
    val categoriaFiscal: String? = null,
    val tarifaImpuestoPorcentaje: Double = 0.0,
    val montoImpuesto: Double = 0.0,
    val totalLinea: Double
)

@JsonClass(generateAdapter = true)
data class VentaDto(
    val id: String,
    val negocioId: String,
    val usuarioId: String? = null,
    val usuarioNombre: String? = null,
    val numeroComprobante: String,
    val fechaVenta: String,
    val monedaVenta: String = "COP",
    val tasaCambioBase: Double = 1.0,
    val subtotalBase: Double = 0.0,
    val totalImpuestos: Double = 0.0,
    val totalDescuentos: Double = 0.0,
    val totalVenta: Double = 0.0,
    val metodoPago: String = "EFECTIVO",
    val estado: String = "COMPLETADA",
    val notas: String? = null,
    val items: List<DetalleVentaDto> = emptyList()
)
