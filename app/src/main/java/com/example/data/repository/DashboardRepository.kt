package com.example.data.repository

import com.example.data.local.db.DashboardCacheEntity
import com.example.data.local.db.DashboardDao
import com.example.data.remote.ApiClient
import com.example.data.remote.api.StockProApi
import com.example.data.remote.dto.DashboardDto
import kotlinx.coroutines.flow.Flow

class DashboardRepository(
    private val api: StockProApi,
    private val dashboardDao: DashboardDao
) {

    // Caché local de métricas generales
    val cachedDashboard: Flow<DashboardCacheEntity?> = dashboardDao.getDashboardCache()

    suspend fun refreshDashboard(currency: String? = null): Result<DashboardDto> {
        return try {
            val response = api.getDashboard(currency)
            if (response.isSuccessful && response.body() != null) {
                val dto = response.body()!!
                // Guardar métricas principales en Room para consulta instantánea offline
                val cacheEntity = DashboardCacheEntity(
                    id = 1,
                    negocioNombre = dto.negocioNombre,
                    moneda = dto.moneda,
                    ventasHoy = dto.ventasHoy,
                    gananciasEstimadasHoy = dto.gananciasEstimadasHoy,
                    cantidadVentasHoy = dto.cantidadVentasHoy,
                    cantidadProductos = dto.cantidadProductos,
                    totalAlertas = dto.alertas.totalAlertas,
                    stockBajoCount = dto.alertas.stockBajoCount,
                    porVencerCount = dto.alertas.porVencerCount,
                    vencidosCount = dto.alertas.vencidosCount
                )
                dashboardDao.insertOrUpdate(cacheEntity)
                Result.success(dto)
            } else {
                Result.failure(Exception(ApiClient.parseError(response)))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
