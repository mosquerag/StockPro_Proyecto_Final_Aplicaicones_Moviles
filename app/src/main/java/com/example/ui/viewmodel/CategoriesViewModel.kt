package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.remote.dto.CategoriaDto
import com.example.data.remote.dto.CreateCategoriaRequestDto
import com.example.data.repository.CategoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CategoriesViewModel(private val categoryRepository: CategoryRepository) : ViewModel() {

    private val _categoriesState = MutableStateFlow<UiState<List<CategoriaDto>>>(UiState.Loading)
    val categoriesState: StateFlow<UiState<List<CategoriaDto>>> = _categoriesState.asStateFlow()

    private val _actionState = MutableStateFlow<UiState<String>>(UiState.Idle)
    val actionState: StateFlow<UiState<String>> = _actionState.asStateFlow()

    init {
        loadCategories()
    }

    fun loadCategories() {
        viewModelScope.launch {
            _categoriesState.value = UiState.Loading
            val result = categoryRepository.getCategories()
            result.fold(
                onSuccess = { _categoriesState.value = UiState.Success(it) },
                onFailure = { _categoriesState.value = UiState.Error(it.message ?: "ERROR_DESCONOCIDO") }
            )
        }
    }

    fun createCategory(nombre: String, descripcion: String?, colorHex: String) {
        if (nombre.isBlank()) {
            _actionState.value = UiState.Error("CAMPOS_OBLIGATORIOS")
            return
        }
        viewModelScope.launch {
            _actionState.value = UiState.Loading
            val result = categoryRepository.createCategory(
                CreateCategoriaRequestDto(
                    nombre = nombre.trim(),
                    descripcion = descripcion?.takeIf { it.isNotBlank() },
                    colorHex = colorHex
                )
            )
            result.fold(
                onSuccess = {
                    _actionState.value = UiState.Success("CATEGORIA_CREADA")
                    loadCategories()
                },
                onFailure = { _actionState.value = UiState.Error(it.message ?: "ERROR_DESCONOCIDO") }
            )
        }
    }

    fun deleteCategory(id: String) {
        viewModelScope.launch {
            _actionState.value = UiState.Loading
            val result = categoryRepository.deleteCategory(id)
            result.fold(
                onSuccess = {
                    _actionState.value = UiState.Success("CATEGORIA_ELIMINADA")
                    loadCategories()
                },
                onFailure = { _actionState.value = UiState.Error(it.message ?: "ERROR_DESCONOCIDO") }
            )
        }
    }
}
