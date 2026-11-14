package com.example.data.local.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products_cache")
data class ProductEntity(
    @PrimaryKey
    val id: String,
    val negocioId: String,
    val categoriaId: String?,
    val categoriaNombre: String?,
    val nombre: String,
    val codigoBarras: String?,
    val unidadMedida: String,
    val costo: Double,
    val precioVenta: Double,
    val categoriaFiscal: String,
    val stockActual: Double,
    val stockMinimo: Double,
    val manejaLotes: Boolean,
    val imagenUrl: String?,
    val fechaVencimiento: String? = null,
    val stockBajo: Boolean,
    val activo: Boolean,
    val lastUpdated: Long = System.currentTimeMillis()
)
