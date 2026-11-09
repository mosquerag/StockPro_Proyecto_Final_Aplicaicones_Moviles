package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.remote.dto.CreateDetalleEntradaRequestDto
import com.example.data.remote.dto.CreateEntradaPrevistaRequestDto
import com.example.data.remote.dto.EntradaPrevistaDto
import com.example.data.remote.dto.ReceiveEntradaRequestDto
import com.example.data.remote.dto.ReceiveItemRequestDto
import com.example.data.repository.InboundRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class InboundViewModel(private val inboundRepository: InboundRepository) : ViewModel() {

    private val _todayState = MutableStateFlow<UiState<List<EntradaPrevistaDto>>>(UiState.Loading)
    val todayState: StateFlow<UiState<List<EntradaPrevistaDto>>> = _todayState.asStateFlow()

    private val _tomorrowState = MutableStateFlow<UiState<List<EntradaPrevistaDto>>>(UiState.Loading)
    val tomorrowState: StateFlow<UiState<List<EntradaPrevistaDto>>> = _tomorrowState.asStateFlow()

    private val _receiveState = MutableStateFlow<UiState<EntradaPrevistaDto>>(UiState.Idle)
    val receiveState: StateFlow<UiState<EntradaPrevistaDto>> = _receiveState.asStateFlow()

    private val _createState = MutableStateFlow<UiState<EntradaPrevistaDto>>(UiState.Idle)
    val createState: StateFlow<UiState<EntradaPrevistaDto>> = _createState.asStateFlow()

    init {
        loadInbounds()
    }

    fun loadInbounds() {
        viewModelScope.launch {
            _todayState.value = UiState.Loading
            inboundRepository.getInboundToday().fold(
                onSuccess = { _todayState.value = UiState.Success(it) },
                onFailure = { _todayState.value = UiState.Error(it.message ?: "ERROR_DESCONOCIDO") }
            )
        }
        viewModelScope.launch {
            _tomorrowState.value = UiState.Loading
            inboundRepository.getInboundTomorrow().fold(
                onSuccess = { _tomorrowState.value = UiState.Success(it) },
                onFailure = { _tomorrowState.value = UiState.Error(it.message ?: "ERROR_DESCONOCIDO") }
            )
        }
    }

    fun receiveInbound(id: String, receivedItems: List<ReceiveItemRequestDto>? = null) {
        viewModelScope.launch {
            _receiveState.value = UiState.Loading
            val req = if (receivedItems != null) ReceiveEntradaRequestDto(items = receivedItems) else null
            val result = inboundRepository.receiveInbound(id, req)
            result.fold(
                onSuccess = {
                    _receiveState.value = UiState.Success(it)
                    loadInbounds()
                },
                onFailure = { _receiveState.value = UiState.Error(it.message ?: "ERROR_DESCONOCIDO") }
            )
        }
    }

    fun createInbound(
        fechaPrevista: String,
        origen: String?,
        notas: String?,
        items: List<CreateDetalleEntradaRequestDto>
    ) {
        if (fechaPrevista.isBlank() || items.isEmpty()) {
            _createState.value = UiState.Error("CAMPOS_OBLIGATORIOS")
            return
        }
        viewModelScope.launch {
            _createState.value = UiState.Loading
            val req = CreateEntradaPrevistaRequestDto(
                fechaPrevista = fechaPrevista,
                origen = origen?.takeIf { it.isNotBlank() },
                notas = notas?.takeIf { it.isNotBlank() },
                items = items
            )
            val result = inboundRepository.createInboundDelivery(req)
            result.fold(
                onSuccess = {
                    _createState.value = UiState.Success(it)
                    loadInbounds()
                },
                onFailure = { _createState.value = UiState.Error(it.message ?: "ERROR_DESCONOCIDO") }
            )
        }
    }
}
