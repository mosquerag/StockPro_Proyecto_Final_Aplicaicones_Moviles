package com.example.data.remote.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ProductoDto(
    val id: String,
    val negocioId: String,
    val categoriaId: String? = null,
    val categoriaNombre: String? = null,
    val nombre: String,
    val codigoBarras: String? = null,
    val unidadMedida: String = "UNIDAD",
    val costo: Double = 0.0,
    val precioVenta: Double = 0.0,
    val categoriaFiscal: String = "EXCLUIDO",
    val tarifaImpuestoId: String? = null,
    val porcentajeImpuestoAplicado: Double? = null,
    val precioIncluyeImpuesto: Boolean = true,
    val stockActual: Double = 0.0,
    val stockMinimo: Double = 5.0,
    val manejaLotes: Boolean = false,
    val imagenUrl: String? = null,
    val fechaVencimiento: String? = null,
    val stockBajo: Boolean = false,
    val activo: Boolean = true,
    val createdAt: String? = null
)

@JsonClass(generateAdapter = true)
data class CreateProductoRequestDto(
    val categoriaId: String,
    val nombre: String,
    val codigoBarras: String? = null,
    val unidadMedida: String = "UNIDAD",
    val costo: Double,
    val precioVenta: Double,
    val categoriaFiscal: String = "EXCLUIDO",
    val tarifaImpuestoId: String? = null,
    val precioIncluyeImpuesto: Boolean = true,
    val stockMinimo: Double = 5.0,
    val manejaLotes: Boolean = false,
    val imagenUrl: String? = null,
    val cantidadInicial: Double? = null,
    val numeroLoteInicial: String? = null,
    val fechaVencimientoInicial: String? = null
)

@JsonClass(generateAdapter = true)
data class UpdateProductoRequestDto(
    val categoriaId: String,
    val nombre: String,
    val codigoBarras: String? = null,
    val unidadMedida: String = "UNIDAD",
    val costo: Double,
    val precioVenta: Double,
    val categoriaFiscal: String = "EXCLUIDO",
    val tarifaImpuestoId: String? = null,
    val precioIncluyeImpuesto: Boolean = true,
    val stockMinimo: Double = 5.0,
    val manejaLotes: Boolean = false,
    val imagenUrl: String? = null,
    val stockActual: Double? = null,
    val fechaVencimiento: String? = null,
    val activo: Boolean = true
)
