package com.example.data

import android.content.Context
import com.example.data.local.db.StockProDatabase
import com.example.data.local.security.TokenManager
import com.example.data.remote.ApiClient
import com.example.data.repository.*

class StockProAppContainer(context: Context) {

    private val appContext = context.applicationContext

    val database: StockProDatabase by lazy {
        StockProDatabase.getInstance(appContext)
    }

    val tokenManager: TokenManager by lazy {
        TokenManager(appContext)
    }

    val api by lazy {
        ApiClient.getApi(appContext)
    }

    val authRepository: AuthRepository by lazy {
        AuthRepository(api, tokenManager, database)
    }

    val productRepository: ProductRepository by lazy {
        ProductRepository(api, database.productDao())
    }

    val categoryRepository: CategoryRepository by lazy {
        CategoryRepository(api)
    }

    val lotRepository: LotRepository by lazy {
        LotRepository(api)
    }

    val saleRepository: SaleRepository by lazy {
        SaleRepository(api)
    }

    val movementRepository: MovementRepository by lazy {
        MovementRepository(api)
    }

    val inboundRepository: InboundRepository by lazy {
        InboundRepository(api)
    }

    val dashboardRepository: DashboardRepository by lazy {
        DashboardRepository(api, database.dashboardDao())
    }

    val reportRepository: ReportRepository by lazy {
        ReportRepository(api)
    }

    val exchangeRateRepository: ExchangeRateRepository by lazy {
        ExchangeRateRepository(api)
    }

    val businessRepository: BusinessRepository by lazy {
        BusinessRepository(api, tokenManager)
    }

    companion object {
        @Volatile
        private var INSTANCE: StockProAppContainer? = null

        fun getInstance(context: Context): StockProAppContainer {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: StockProAppContainer(context).also { INSTANCE = it }
            }
        }
    }
}
