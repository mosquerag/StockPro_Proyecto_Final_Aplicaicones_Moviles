package com.example.data.remote.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GananciasReportDto(
    val totalVentas: Double = 0.0,
    val totalCostos: Double = 0.0,
    val gananciaBruta: Double = 0.0,
    val margenPorcentual: Double = 0.0,
    val moneda: String? = null
)

@JsonClass(generateAdapter = true)
data class ProductoRankingDto(
    val productoId: String,
    val productoNombre: String,
    val categoriaNombre: String? = null,
    val cantidadVendida: Double = 0.0,
    val totalRecaudado: Double = 0.0,
    val gananciaAportada: Double = 0.0
)

@JsonClass(generateAdapter = true)
data class PerdidaItemDto(
    val movimientoId: String? = null,
    val productoId: String,
    val productoNombre: String,
    val tipoMovimiento: String,
    val cantidad: Double,
    val costoReferencia: Double = 0.0,
    val costoTotalPerdido: Double = 0.0,
    val motivo: String,
    val fecha: String? = null
)

@JsonClass(generateAdapter = true)
data class PerdidasReportDto(
    val totalUnidadesPerdidas: Double = 0.0,
    val costoTotalPerdidas: Double = 0.0,
    val moneda: String? = null,
    val detalles: List<PerdidaItemDto> = emptyList()
)

@JsonClass(generateAdapter = true)
data class ProductividadProductoDto(
    val productoId: String,
    val productoNombre: String,
    val categoriaNombre: String? = null,
    val unidadesVendidas: Double = 0.0,
    val facturacionTotal: Double = 0.0,
    val costoTotal: Double = 0.0,
    val gananciaNeta: Double = 0.0,
    val margenPorcentual: Double = 0.0
)

@JsonClass(generateAdapter = true)
data class ImpuestoCobradoDto(
    val categoriaFiscal: String,
    val tarifaPorcentaje: Double = 0.0,
    val baseGravable: Double = 0.0,
    val montoCobrado: Double = 0.0,
    val moneda: String? = null
)

@JsonClass(generateAdapter = true)
data class ReporteGeneralDto(
    val periodo: String = "30D",
    val fechaInicio: String? = null,
    val fechaFin: String? = null,
    val monedaReporte: String = "COP",
    val ganancias: GananciasReportDto = GananciasReportDto(),
    val masVendidos: List<ProductoRankingDto> = emptyList(),
    val menosVendidos: List<ProductoRankingDto> = emptyList(),
    val perdidasYBajas: PerdidasReportDto = PerdidasReportDto(),
    val productividadProductos: List<ProductividadProductoDto> = emptyList(),
    val impuestosCobrados: List<ImpuestoCobradoDto> = emptyList()
)
