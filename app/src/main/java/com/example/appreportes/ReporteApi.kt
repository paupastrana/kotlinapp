package com.example.appreportes

import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.GET

interface ReporteApi {
    @POST("reportes") //endpoint para guardar
    suspend fun enviarReporte(@Body reporte: Reporte)

    @GET("reportes") //endpoint para lista
    suspend fun obtenerReportes(): List<Reporte>
}