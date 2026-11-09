package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.remote.dto.*
import com.example.data.repository.AuthRepository
import com.example.data.repository.BusinessRepository
import com.example.data.repository.ExchangeRateRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val authRepository: AuthRepository,
    private val exchangeRateRepository: ExchangeRateRepository,
    private val businessRepository: BusinessRepository
) : ViewModel() {

    private val _ratesState = MutableStateFlow<UiState<List<TasaCambioDto>>>(UiState.Idle)
    val ratesState: StateFlow<UiState<List<TasaCambioDto>>> = _ratesState.asStateFlow()

    private val _businessState = MutableStateFlow<UiState<BusinessDetailsDto>>(UiState.Idle)
    val businessState: StateFlow<UiState<BusinessDetailsDto>> = _businessState.asStateFlow()

    private val _taxRatesState = MutableStateFlow<UiState<List<TarifaImpuestoDto>>>(UiState.Idle)
    val taxRatesState: StateFlow<UiState<List<TarifaImpuestoDto>>> = _taxRatesState.asStateFlow()

    init {
        loadBusiness()
    }

    fun getBusinessName(): String = authRepository.getBusinessName()
    fun getBusinessCurrency(): String = authRepository.getBusinessCurrency()
    fun getUserEmail(): String = authRepository.getUserEmail()
    fun getUserRole(): String = authRepository.getUserRole()

    fun loadBusiness() {
        viewModelScope.launch {
            _businessState.value = UiState.Loading
            businessRepository.getBusiness().fold(
                onSuccess = { business ->
                    _businessState.value = UiState.Success(business)
                    loadTaxRates(business.pais)
                },
                onFailure = {
                    _businessState.value = UiState.Error(it.message ?: "ERROR_DESCONOCIDO")
                }
            )
        }
    }

    fun updateBusiness(
        nombre: String,
        pais: String,
        estadoRegion: String?,
        monedaBase: String,
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            _businessState.value = UiState.Loading
            val req = UpdateBusinessRequestDto(
                nombre = nombre,
                pais = pais,
                estadoRegion = estadoRegion,
                monedaBase = monedaBase
            )
            businessRepository.updateBusiness(req).fold(
                onSuccess = { updated ->
                    _businessState.value = UiState.Success(updated)
                    loadTaxRates(updated.pais)
                    onSuccess()
                },
                onFailure = {
                    _businessState.value = UiState.Error(it.message ?: "ERROR_DESCONOCIDO")
                }
            )
        }
    }

    fun loadTaxRates(country: String? = null) {
        viewModelScope.launch {
            _taxRatesState.value = UiState.Loading
            businessRepository.getTaxRates(country).fold(
                onSuccess = { _taxRatesState.value = UiState.Success(it) },
                onFailure = { _taxRatesState.value = UiState.Error(it.message ?: "ERROR_DESCONOCIDO") }
            )
        }
    }

    fun loadRates() {
        viewModelScope.launch {
            _ratesState.value = UiState.Loading
            exchangeRateRepository.getExchangeRates().fold(
                onSuccess = { _ratesState.value = UiState.Success(it) },
                onFailure = { _ratesState.value = UiState.Error(it.message ?: "ERROR_DESCONOCIDO") }
            )
        }
    }

    fun setManualRate(origen: String, destino: String, tasa: Double) {
        viewModelScope.launch {
            _ratesState.value = UiState.Loading
            val req = ManualTasaCambioRequestDto(origen, destino, tasa)
            exchangeRateRepository.setManualExchangeRate(req).fold(
                onSuccess = { loadRates() },
                onFailure = { _ratesState.value = UiState.Error(it.message ?: "ERROR_DESCONOCIDO") }
            )
        }
    }

    fun logout(onComplete: () -> Unit) {
        viewModelScope.launch {
            authRepository.logout()
            onComplete()
        }
    }
}
