package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.db.DashboardCacheEntity
import com.example.data.remote.dto.DashboardDto
import com.example.data.repository.DashboardRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val dashboardRepository: DashboardRepository
) : ViewModel() {

    // Caché local de Room
    val cachedDashboard: StateFlow<DashboardCacheEntity?> = dashboardRepository.cachedDashboard
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    private val _dashboardState = MutableStateFlow<UiState<DashboardDto>>(UiState.Loading)
    val dashboardState: StateFlow<UiState<DashboardDto>> = _dashboardState.asStateFlow()

    fun loadDashboard(currency: String? = null) {
        viewModelScope.launch {
            _dashboardState.value = UiState.Loading
            val result = dashboardRepository.refreshDashboard(currency)
            result.fold(
                onSuccess = { _dashboardState.value = UiState.Success(it) },
                onFailure = { _dashboardState.value = UiState.Error(it.message ?: "ERROR_DESCONOCIDO") }
            )
        }
    }
}
