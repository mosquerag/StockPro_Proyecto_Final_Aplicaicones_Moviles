package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.remote.dto.ReporteGeneralDto
import com.example.data.repository.ReportRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ReportsViewModel(private val reportRepository: ReportRepository) : ViewModel() {

    private val _reportState = MutableStateFlow<UiState<ReporteGeneralDto>>(UiState.Loading)
    val reportState: StateFlow<UiState<ReporteGeneralDto>> = _reportState.asStateFlow()

    fun loadReport(period: String = "30D", currency: String? = null) {
        viewModelScope.launch {
            _reportState.value = UiState.Loading
            val result = reportRepository.getReport(period, currency)
            result.fold(
                onSuccess = { _reportState.value = UiState.Success(it) },
                onFailure = { _reportState.value = UiState.Error(it.message ?: "ERROR_DESCONOCIDO") }
            )
        }
    }
}
