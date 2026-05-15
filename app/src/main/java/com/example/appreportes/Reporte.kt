package com.example.appreportes

data class Reporte(
    val id: Int? = null,
    val titulo: String,
    val descripcion: String,
    val latitud: Double,
    val longitud: Double,
    val imagenBase64: String
)