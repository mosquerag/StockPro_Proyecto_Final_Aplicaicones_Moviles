package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.remote.dto.CategoriaDto
import com.example.data.remote.dto.CreateProductoRequestDto
import com.example.data.remote.dto.ProductoDto
import com.example.data.remote.dto.UpdateProductoRequestDto
import com.example.data.repository.CategoryRepository
import com.example.data.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NewEditProductViewModel(
    private val productRepository: ProductRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _categories = MutableStateFlow<List<CategoriaDto>>(emptyList())
    val categories: StateFlow<List<CategoriaDto>> = _categories.asStateFlow()

    private val _product = MutableStateFlow<ProductoDto?>(null)
    val product: StateFlow<ProductoDto?> = _product.asStateFlow()

    private val _saveState = MutableStateFlow<UiState<ProductoDto>>(UiState.Idle)
    val saveState: StateFlow<UiState<ProductoDto>> = _saveState.asStateFlow()

    init {
        loadCategories()
    }

    fun loadCategories() {
        viewModelScope.launch {
            categoryRepository.getCategories().onSuccess { _categories.value = it }
        }
    }

    fun loadProduct(productId: String) {
        viewModelScope.launch {
            productRepository.getProductById(productId).onSuccess { _product.value = it }
        }
    }

    fun saveProduct(
        productId: String?,
        categoriaId: String,
        nombre: String,
        codigoBarras: String?,
        costo: Double,
        precioVenta: Double,
        stockMinimo: Double,
        manejaLotes: Boolean,
        cantidadInicial: Double?,
        numeroLoteInicial: String?,
        fechaVencimientoInicial: String?
    ) {
        if (nombre.isBlank() || categoriaId.isBlank() || costo < 0 || precioVenta < 0) {
            _saveState.value = UiState.Error("CAMPOS_OBLIGATORIOS")
            return
        }

        viewModelScope.launch {
            _saveState.value = UiState.Loading
            if (productId.isNullOrBlank()) {
                val req = CreateProductoRequestDto(
                    categoriaId = categoriaId,
                    nombre = nombre.trim(),
                    codigoBarras = codigoBarras?.takeIf { it.isNotBlank() },
                    costo = costo,
                    precioVenta = precioVenta,
                    stockMinimo = stockMinimo,
                    manejaLotes = manejaLotes,
                    cantidadInicial = cantidadInicial,
                    numeroLoteInicial = numeroLoteInicial?.takeIf { it.isNotBlank() },
                    fechaVencimientoInicial = fechaVencimientoInicial?.takeIf { it.isNotBlank() }
                )
                val result = productRepository.createProduct(req)
                result.fold(
                    onSuccess = { _saveState.value = UiState.Success(it) },
                    onFailure = { _saveState.value = UiState.Error(it.message ?: "ERROR_DESCONOCIDO") }
                )
            } else {
                val req = UpdateProductoRequestDto(
                    categoriaId = categoriaId,
                    nombre = nombre.trim(),
                    codigoBarras = codigoBarras?.takeIf { it.isNotBlank() },
                    costo = costo,
                    precioVenta = precioVenta,
                    stockMinimo = stockMinimo,
                    manejaLotes = manejaLotes,
                    activo = true
                )
                val result = productRepository.updateProduct(productId, req)
                result.fold(
                    onSuccess = { _saveState.value = UiState.Success(it) },
                    onFailure = { _saveState.value = UiState.Error(it.message ?: "ERROR_DESCONOCIDO") }
                )
            }
        }
    }
}
