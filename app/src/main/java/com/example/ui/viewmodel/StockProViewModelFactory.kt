package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.data.StockProAppContainer

class StockProViewModelFactory(private val container: StockProAppContainer) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> {
                AuthViewModel(container.authRepository) as T
            }
            modelClass.isAssignableFrom(DashboardViewModel::class.java) -> {
                DashboardViewModel(container.dashboardRepository) as T
            }
            modelClass.isAssignableFrom(ProductsViewModel::class.java) -> {
                ProductsViewModel(container.productRepository, container.categoryRepository) as T
            }
            modelClass.isAssignableFrom(ProductDetailViewModel::class.java) -> {
                ProductDetailViewModel(container.productRepository, container.lotRepository) as T
            }
            modelClass.isAssignableFrom(NewEditProductViewModel::class.java) -> {
                NewEditProductViewModel(container.productRepository, container.categoryRepository) as T
            }
            modelClass.isAssignableFrom(CategoriesViewModel::class.java) -> {
                CategoriesViewModel(container.categoryRepository) as T
            }
            modelClass.isAssignableFrom(MovementsViewModel::class.java) -> {
                MovementsViewModel(container.movementRepository) as T
            }
            modelClass.isAssignableFrom(RegisterSaleViewModel::class.java) -> {
                RegisterSaleViewModel(container.productRepository, container.saleRepository) as T
            }
            modelClass.isAssignableFrom(SaleDetailViewModel::class.java) -> {
                SaleDetailViewModel(container.saleRepository) as T
            }
            modelClass.isAssignableFrom(InboundViewModel::class.java) -> {
                InboundViewModel(container.inboundRepository) as T
            }
            modelClass.isAssignableFrom(AlertsViewModel::class.java) -> {
                AlertsViewModel(container.productRepository, container.lotRepository) as T
            }
            modelClass.isAssignableFrom(ReportsViewModel::class.java) -> {
                ReportsViewModel(container.reportRepository) as T
            }
            modelClass.isAssignableFrom(SettingsViewModel::class.java) -> {
                SettingsViewModel(container.authRepository, container.exchangeRateRepository, container.businessRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
