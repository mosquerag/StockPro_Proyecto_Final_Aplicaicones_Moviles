package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.db.ProductEntity
import com.example.data.remote.dto.CategoriaDto
import com.example.data.remote.dto.ProductoDto
import com.example.data.repository.CategoryRepository
import com.example.data.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProductsViewModel(
    private val productRepository: ProductRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    // Caché reactivo de Room
    val cachedProducts: StateFlow<List<ProductEntity>> = productRepository.cachedProducts
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _productsState = MutableStateFlow<UiState<List<ProductoDto>>>(UiState.Loading)
    val productsState: StateFlow<UiState<List<ProductoDto>>> = _productsState.asStateFlow()

    private val _categories = MutableStateFlow<List<CategoriaDto>>(emptyList())
    val categories: StateFlow<List<CategoriaDto>> = _categories.asStateFlow()

    init {
        loadCategories()
        refreshProducts()
    }

    fun loadCategories() {
        viewModelScope.launch {
            val result = categoryRepository.getCategories()
            result.onSuccess { _categories.value = it }
        }
    }

    fun refreshProducts(categoriaId: String? = null) {
        viewModelScope.launch {
            _productsState.value = UiState.Loading
            val result = productRepository.refreshProducts(categoriaId)
            result.fold(
                onSuccess = { _productsState.value = UiState.Success(it) },
                onFailure = { _productsState.value = UiState.Error(it.message ?: "ERROR_DESCONOCIDO") }
            )
        }
    }
}
