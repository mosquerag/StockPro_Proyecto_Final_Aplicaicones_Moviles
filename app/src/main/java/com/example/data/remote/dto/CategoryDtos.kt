package com.example.data.remote.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CategoriaDto(
    val id: String,
    val negocioId: String,
    val nombre: String,
    val descripcion: String? = null,
    val colorHex: String = "#087F5B",
    val activo: Boolean = true
)

@JsonClass(generateAdapter = true)
data class CreateCategoriaRequestDto(
    val nombre: String,
    val descripcion: String? = null,
    val colorHex: String = "#087F5B"
)
