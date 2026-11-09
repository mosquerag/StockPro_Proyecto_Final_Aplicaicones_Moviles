package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.remote.dto.ProductoDto
import com.example.data.remote.dto.VencimientoAlertDto
import com.example.data.repository.LotRepository
import com.example.data.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AlertsViewModel(
    private val productRepository: ProductRepository,
    private val lotRepository: LotRepository
) : ViewModel() {

    private val _lowStockState = MutableStateFlow<UiState<List<ProductoDto>>>(UiState.Loading)
    val lowStockState: StateFlow<UiState<List<ProductoDto>>> = _lowStockState.asStateFlow()

    private val _expiringLotsState = MutableStateFlow<UiState<List<VencimientoAlertDto>>>(UiState.Loading)
    val expiringLotsState: StateFlow<UiState<List<VencimientoAlertDto>>> = _expiringLotsState.asStateFlow()

    private val _expiredLotsState = MutableStateFlow<UiState<List<VencimientoAlertDto>>>(UiState.Loading)
    val expiredLotsState: StateFlow<UiState<List<VencimientoAlertDto>>> = _expiredLotsState.asStateFlow()

    private val _bajaState = MutableStateFlow<UiState<String>>(UiState.Idle)
    val bajaState: StateFlow<UiState<String>> = _bajaState.asStateFlow()

    init {
        loadAlerts()
    }

    fun loadAlerts() {
        viewModelScope.launch {
            _lowStockState.value = UiState.Loading
            productRepository.getLowStockProducts().fold(
                onSuccess = { _lowStockState.value = UiState.Success(it) },
                onFailure = { _lowStockState.value = UiState.Error(it.message ?: "ERROR_DESCONOCIDO") }
            )
        }
        viewModelScope.launch {
            _expiringLotsState.value = UiState.Loading
            lotRepository.getExpiringLots().fold(
                onSuccess = { _expiringLotsState.value = UiState.Success(it) },
                onFailure = { _expiringLotsState.value = UiState.Error(it.message ?: "ERROR_DESCONOCIDO") }
            )
        }
        viewModelScope.launch {
            _expiredLotsState.value = UiState.Loading
            lotRepository.getExpiredLots().fold(
                onSuccess = { _expiredLotsState.value = UiState.Success(it) },
                onFailure = { _expiredLotsState.value = UiState.Error(it.message ?: "ERROR_DESCONOCIDO") }
            )
        }
    }

    fun darDeBajaLote(loteId: String, motivo: String) {
        viewModelScope.launch {
            _bajaState.value = UiState.Loading
            lotRepository.darDeBajaLote(loteId, motivo).fold(
                onSuccess = {
                    _bajaState.value = UiState.Success("LOTE_DADO_DE_BAJA")
                    loadAlerts()
                },
                onFailure = { _bajaState.value = UiState.Error(it.message ?: "ERROR_DESCONOCIDO") }
            )
        }
    }
}
