package com.example.data.remote.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AlertasResumenDto(
    val totalAlertas: Int = 0,
    val stockBajoCount: Int = 0,
    val porVencerCount: Int = 0,
    val vencidosCount: Int = 0,
    val stockBajo: List<ProductoDto> = emptyList(),
    val porVencer: List<VencimientoAlertDto> = emptyList(),
    val vencidos: List<VencimientoAlertDto> = emptyList()
)

@JsonClass(generateAdapter = true)
data class DashboardDto(
    val negocioNombre: String = "",
    val moneda: String = "COP",
    val ventasHoy: Double = 0.0,
    val gananciasEstimadasHoy: Double = 0.0,
    val gananciasHoy: Double = 0.0,
    val gananciasSemanal: Double = 0.0,
    val gananciasQuincenal: Double = 0.0,
    val gananciasMensual: Double = 0.0,
    val gananciasAnual: Double = 0.0,
    val cantidadVentasHoy: Int = 0,
    val cantidadProductos: Long = 0,
    val alertas: AlertasResumenDto = AlertasResumenDto(),
    val ultimasVentas: List<VentaDto> = emptyList(),
    val entradasHoy: List<EntradaPrevistaDto> = emptyList(),
    val entradasManana: List<EntradaPrevistaDto> = emptyList()
)
