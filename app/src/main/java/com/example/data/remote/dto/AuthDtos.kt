package com.example.data.remote.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LoginRequestDto(
    val correo: String,
    val password: String
)

@JsonClass(generateAdapter = true)
data class RegisterBusinessRequestDto(
    val nombrePropietario: String,
    val correo: String,
    val password: String,
    val telefono: String? = null,
    val nombreNegocio: String,
    val tipoNegocio: String,
    val identificacionFiscal: String? = null,
    val pais: String,
    val estadoRegion: String? = null,
    val monedaBase: String
)

@JsonClass(generateAdapter = true)
data class RefreshTokenRequestDto(
    val refreshToken: String
)

@JsonClass(generateAdapter = true)
data class NegocioSummaryDto(
    val id: String,
    val nombre: String,
    val tipoNegocio: String,
    val pais: String,
    val estadoRegion: String? = null,
    val monedaBase: String,
    val rol: String
)

@JsonClass(generateAdapter = true)
data class AuthResponseDto(
    val accessToken: String,
    val refreshToken: String,
    val tokenType: String = "Bearer",
    val expiresInMs: Long = 0,
    val usuarioId: String,
    val nombre: String,
    val correo: String,
    val negocioActivo: NegocioSummaryDto? = null,
    val negocios: List<NegocioSummaryDto> = emptyList()
)
