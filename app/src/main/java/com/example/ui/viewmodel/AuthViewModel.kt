package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.remote.dto.AuthResponseDto
import com.example.data.remote.dto.RegisterBusinessRequestDto
import com.example.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _loginState = MutableStateFlow<UiState<AuthResponseDto>>(UiState.Idle)
    val loginState: StateFlow<UiState<AuthResponseDto>> = _loginState.asStateFlow()

    private val _registerState = MutableStateFlow<UiState<AuthResponseDto>>(UiState.Idle)
    val registerState: StateFlow<UiState<AuthResponseDto>> = _registerState.asStateFlow()

    fun login(correo: String, pass: String) {
        if (correo.isBlank() || pass.isBlank()) {
            _loginState.value = UiState.Error("CAMPOS_OBLIGATORIOS")
            return
        }
        viewModelScope.launch {
            _loginState.value = UiState.Loading
            val result = authRepository.login(correo.trim(), pass)
            result.fold(
                onSuccess = { _loginState.value = UiState.Success(it) },
                onFailure = { _loginState.value = UiState.Error(it.message ?: "ERROR_DESCONOCIDO") }
            )
        }
    }

    fun register(
        nombrePropietario: String,
        correo: String,
        pass: String,
        telefono: String?,
        nombreNegocio: String,
        tipoNegocio: String,
        pais: String,
        estadoRegion: String?,
        monedaBase: String
    ) {
        if (nombrePropietario.isBlank() || correo.isBlank() || pass.isBlank() || nombreNegocio.isBlank()) {
            _registerState.value = UiState.Error("CAMPOS_OBLIGATORIOS")
            return
        }
        viewModelScope.launch {
            _registerState.value = UiState.Loading
            val request = RegisterBusinessRequestDto(
                nombrePropietario = nombrePropietario.trim(),
                correo = correo.trim(),
                password = pass,
                telefono = telefono?.takeIf { it.isNotBlank() },
                nombreNegocio = nombreNegocio.trim(),
                tipoNegocio = tipoNegocio,
                pais = pais,
                estadoRegion = estadoRegion?.takeIf { it.isNotBlank() },
                monedaBase = monedaBase
            )
            val result = authRepository.registerBusiness(request)
            result.fold(
                onSuccess = { _registerState.value = UiState.Success(it) },
                onFailure = { _registerState.value = UiState.Error(it.message ?: "ERROR_DESCONOCIDO") }
            )
        }
    }

    fun resetState() {
        _loginState.value = UiState.Idle
        _registerState.value = UiState.Idle
    }

    fun isLoggedIn(): Boolean = authRepository.isLoggedIn()
}
