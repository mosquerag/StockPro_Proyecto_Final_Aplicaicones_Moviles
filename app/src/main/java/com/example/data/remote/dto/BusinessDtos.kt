package com.example.data.remote.dto

import com.squareup.moshi.JsonClass
import java.math.BigDecimal

@JsonClass(generateAdapter = true)
data class TarifaImpuestoDto(
    val id: String,
    val pais: String,
    val estadoRegion: String? = null,
    val categoriaFiscal: String,
    val tipoImpuesto: String,
    val porcentaje: BigDecimal,
    val vigenciaAnio: Int = 2026,
    val fuente: String? = null,
    val descripcion: String? = null
)

@JsonClass(generateAdapter = true)
data class BusinessDetailsDto(
    val id: String,
    val nombre: String,
    val tipoNegocio: String? = null,
    val identificacionFiscal: String? = null,
    val pais: String,
    val estadoRegion: String? = null,
    val monedaBase: String,
    val zonaHoraria: String? = null,
    val permiteStockNegativo: Boolean = false
)

@JsonClass(generateAdapter = true)
data class UpdateBusinessRequestDto(
    val nombre: String,
    val pais: String,
    val estadoRegion: String? = null,
    val monedaBase: String,
    val tipoNegocio: String? = null,
    val identificacionFiscal: String? = null,
    val permiteStockNegativo: Boolean? = null
)
