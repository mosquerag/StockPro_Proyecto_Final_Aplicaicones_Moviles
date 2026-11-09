package com.example.ui.viewmodel

sealed interface UiState<out T> {
    object Idle : UiState<Nothing>
    object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val code: String, val message: String? = null) : UiState<Nothing>
}
