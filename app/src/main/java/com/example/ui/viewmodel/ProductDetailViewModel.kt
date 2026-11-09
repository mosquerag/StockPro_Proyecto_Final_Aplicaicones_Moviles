package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.remote.dto.LoteDto
import com.example.data.remote.dto.ProductoDto
import com.example.data.repository.LotRepository
import com.example.data.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProductDetailViewModel(
    private val productRepository: ProductRepository,
    private val lotRepository: LotRepository
) : ViewModel() {

    private val _productState = MutableStateFlow<UiState<ProductoDto>>(UiState.Loading)
    val productState: StateFlow<UiState<ProductoDto>> = _productState.asStateFlow()

    private val _lotsState = MutableStateFlow<UiState<List<LoteDto>>>(UiState.Loading)
    val lotsState: StateFlow<UiState<List<LoteDto>>> = _lotsState.asStateFlow()

    private val _actionState = MutableStateFlow<UiState<String>>(UiState.Idle)
    val actionState: StateFlow<UiState<String>> = _actionState.asStateFlow()

    fun loadProduct(productId: String) {
        viewModelScope.launch {
            _productState.value = UiState.Loading
            val result = productRepository.getProductById(productId)
            result.fold(
                onSuccess = { _productState.value = UiState.Success(it) },
                onFailure = { _productState.value = UiState.Error(it.message ?: "ERROR_DESCONOCIDO") }
            )
        }
        loadLots(productId)
    }

    fun loadLots(productId: String) {
        viewModelScope.launch {
            _lotsState.value = UiState.Loading
            val result = lotRepository.getLotsByProduct(productId)
            result.fold(
                onSuccess = { _lotsState.value = UiState.Success(it) },
                onFailure = { _lotsState.value = UiState.Error(it.message ?: "ERROR_DESCONOCIDO") }
            )
        }
    }

    fun darDeBajaLote(productId: String, loteId: String, motivo: String) {
        viewModelScope.launch {
            _actionState.value = UiState.Loading
            val result = lotRepository.darDeBajaLote(loteId, motivo)
            result.fold(
                onSuccess = {
                    _actionState.value = UiState.Success("LOTE_DADO_DE_BAJA")
                    loadProduct(productId)
                },
                onFailure = { _actionState.value = UiState.Error(it.message ?: "ERROR_DESCONOCIDO") }
            )
        }
    }

    fun deleteProduct(productId: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _actionState.value = UiState.Loading
            val result = productRepository.deleteProduct(productId)
            result.fold(
                onSuccess = {
                    _actionState.value = UiState.Success("PRODUCTO_ELIMINADO")
                    onSuccess()
                },
                onFailure = { _actionState.value = UiState.Error(it.message ?: "ERROR_DESCONOCIDO") }
            )
        }
    }
}
