package com.example.data.local.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "dashboard_cache")
data class DashboardCacheEntity(
    @PrimaryKey
    val id: Int = 1,
    val negocioNombre: String,
    val moneda: String,
    val ventasHoy: Double,
    val gananciasEstimadasHoy: Double,
    val cantidadProductos: Long,
    val totalAlertas: Int,
    val stockBajoCount: Int,
    val porVencerCount: Int,
    val vencidosCount: Int,
    val lastUpdated: Long = System.currentTimeMillis()
)
