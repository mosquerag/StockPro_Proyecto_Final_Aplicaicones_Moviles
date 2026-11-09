package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.remote.dto.VentaDto
import com.example.data.repository.SaleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SaleDetailViewModel(private val saleRepository: SaleRepository) : ViewModel() {

    private val _saleState = MutableStateFlow<UiState<VentaDto>>(UiState.Loading)
    val saleState: StateFlow<UiState<VentaDto>> = _saleState.asStateFlow()

    fun loadSale(saleId: String) {
        viewModelScope.launch {
            _saleState.value = UiState.Loading
            val result = saleRepository.getSaleById(saleId)
            result.fold(
                onSuccess = { _saleState.value = UiState.Success(it) },
                onFailure = { _saleState.value = UiState.Error(it.message ?: "ERROR_DESCONOCIDO") }
            )
        }
    }
}
