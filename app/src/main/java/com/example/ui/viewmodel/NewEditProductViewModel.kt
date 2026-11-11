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

    private fun formatToIsoDate(dateStr: String?): String? {
        if (dateStr.isNullOrBlank()) return null
        val clean = dateStr.trim()
        if (clean.matches(Regex("""\d{4}-\d{2}-\d{2}"""))) return clean
        val parts = clean.split("/", "-")
        if (parts.size == 3) {
            if (parts[0].length == 2 && parts[2].length == 4) {
                val day = parts[0].padStart(2, '0')
                val month = parts[1].padStart(2, '0')
                val year = parts[2]
                return "$year-$month-$day"
            }
        }
        return clean
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
        fechaVencimientoInicial: String?,
        imagenUrl: String? = null
    ) {
        if (nombre.isBlank() || categoriaId.isBlank() || costo < 0 || precioVenta < 0) {
            _saveState.value = UiState.Error("CAMPOS_OBLIGATORIOS")
            return
        }

        val formattedDate = formatToIsoDate(fechaVencimientoInicial)

        viewModelScope.launch {
            _saveState.value = UiState.Loading
            if (productId.isNullOrBlank()) {
                val req = CreateProductoRequestDto(
                    categoriaId = categoriaId,
                    nombre = nombre.trim(),
                    codigoBarras = codigoBarras?.takeIf { it.isNotBlank() },
                    costo = costo,
                    precioVenta = precioVenta,
                    categoriaFiscal = "GENERAL",
                    stockMinimo = stockMinimo,
                    manejaLotes = manejaLotes,
                    imagenUrl = imagenUrl?.takeIf { it.isNotBlank() },
                    cantidadInicial = cantidadInicial,
                    numeroLoteInicial = numeroLoteInicial?.takeIf { it.isNotBlank() },
                    fechaVencimientoInicial = formattedDate?.takeIf { it.isNotBlank() }
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
                    categoriaFiscal = "GENERAL",
                    stockMinimo = stockMinimo,
                    manejaLotes = manejaLotes,
                    imagenUrl = imagenUrl?.takeIf { it.isNotBlank() },
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
