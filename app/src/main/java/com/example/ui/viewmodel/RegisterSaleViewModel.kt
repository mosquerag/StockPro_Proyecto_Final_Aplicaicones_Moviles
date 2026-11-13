package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.remote.dto.CreateVentaItemRequestDto
import com.example.data.remote.dto.CreateVentaRequestDto
import com.example.data.remote.dto.ProductoDto
import com.example.data.remote.dto.VentaDto
import com.example.data.repository.ProductRepository
import com.example.data.repository.SaleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RegisterSaleViewModel(
    private val productRepository: ProductRepository,
    private val saleRepository: SaleRepository
) : ViewModel() {

    private val _products = MutableStateFlow<List<ProductoDto>>(emptyList())
    val products: StateFlow<List<ProductoDto>> = _products.asStateFlow()

    private val _cart = MutableStateFlow<Map<String, Double>>(emptyMap()) // productId -> quantity
    val cart: StateFlow<Map<String, Double>> = _cart.asStateFlow()

    private val _saleState = MutableStateFlow<UiState<VentaDto>>(UiState.Idle)
    val saleState: StateFlow<UiState<VentaDto>> = _saleState.asStateFlow()

    init {
        loadProducts()
    }

    fun loadProducts() {
        viewModelScope.launch {
            productRepository.refreshProducts().onSuccess {
                _products.value = it.filter { p -> p.activo && p.stockActual > 0 }
            }
        }
    }

    fun updateCart(productId: String, quantity: Double) {
        val current = _cart.value.toMutableMap()
        if (quantity <= 0) {
            current.remove(productId)
        } else {
            current[productId] = quantity
        }
        _cart.value = current
    }

    fun clearCart() {
        _cart.value = emptyMap()
    }

    fun submitSale(currencyCode: String, paymentMethod: String = "EFECTIVO", notes: String? = null) {
        val currentCart = _cart.value
        if (currentCart.isEmpty()) {
            _saleState.value = UiState.Error("CARRITO_VACIO")
            return
        }

        viewModelScope.launch {
            _saleState.value = UiState.Loading
            val items = currentCart.map { (prodId, qty) ->
                CreateVentaItemRequestDto(
                    productoId = prodId,
                    cantidad = qty
                )
            }
            val request = CreateVentaRequestDto(
                items = items,
                monedaVenta = currencyCode,
                metodoPago = paymentMethod,
                notas = notes
            )
            val result = saleRepository.createSale(request)
            result.fold(
                onSuccess = {
                    clearCart()
                    _saleState.value = UiState.Success(it)
                },
                onFailure = { _saleState.value = UiState.Error(it.message ?: "ERROR_DESCONOCIDO") }
            )
        }
    }

    fun onSaleSuccessConsumed() {
        _saleState.value = UiState.Idle
    }
}
