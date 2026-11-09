package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.remote.dto.CreateMovimientoRequestDto
import com.example.data.remote.dto.MovimientoInventarioDto
import com.example.data.repository.MovementRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MovementsViewModel(private val movementRepository: MovementRepository) : ViewModel() {

    private val _movementsState = MutableStateFlow<UiState<List<MovimientoInventarioDto>>>(UiState.Loading)
    val movementsState: StateFlow<UiState<List<MovimientoInventarioDto>>> = _movementsState.asStateFlow()

    private val _recordState = MutableStateFlow<UiState<MovimientoInventarioDto>>(UiState.Idle)
    val recordState: StateFlow<UiState<MovimientoInventarioDto>> = _recordState.asStateFlow()

    init {
        loadMovements()
    }

    fun loadMovements() {
        viewModelScope.launch {
            _movementsState.value = UiState.Loading
            val result = movementRepository.getMovements()
            result.fold(
                onSuccess = { _movementsState.value = UiState.Success(it) },
                onFailure = { _movementsState.value = UiState.Error(it.message ?: "ERROR_DESCONOCIDO") }
            )
        }
    }

    fun recordMovement(productoId: String, tipo: String, cantidad: Double, motivo: String, loteId: String? = null) {
        if (productoId.isBlank() || cantidad <= 0 || motivo.isBlank()) {
            _recordState.value = UiState.Error("CAMPOS_OBLIGATORIOS")
            return
        }
        viewModelScope.launch {
            _recordState.value = UiState.Loading
            val req = CreateMovimientoRequestDto(
                productoId = productoId,
                loteId = loteId?.takeIf { it.isNotBlank() },
                tipoMovimiento = tipo,
                cantidad = cantidad,
                motivo = motivo.trim()
            )
            val result = movementRepository.createMovement(req)
            result.fold(
                onSuccess = {
                    _recordState.value = UiState.Success(it)
                    loadMovements()
                },
                onFailure = { _recordState.value = UiState.Error(it.message ?: "ERROR_DESCONOCIDO") }
            )
        }
    }
}
