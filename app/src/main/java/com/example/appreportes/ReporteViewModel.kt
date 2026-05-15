package com.example.appreportes

import android.graphics.Bitmap
import android.util.Base64
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.ByteArrayOutputStream


class ReporteViewModel : ViewModel() {

    // estados
    var titulo by mutableStateOf("")
    var descripcion by mutableStateOf("")
    var ubicacionText by mutableStateOf("Ubicación no obtenida")
    var latitud by mutableDoubleStateOf(0.0)
    var longitud by mutableDoubleStateOf(0.0)
    var fotoBase64 by mutableStateOf("")

    //lista teportes
    var listaReportes = mutableStateListOf<Reporte>()

    // regrofit
    private val retrofit = Retrofit.Builder()
        .baseUrl("http://10.0.2.2:3000/") // IP para conectar el emulador con tu servidor local
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val api = retrofit.create(ReporteApi::class.java)

    //procesamiento de imagen
    fun convertirImagenABase64(bitmap: Bitmap) {
        val outputStream = ByteArrayOutputStream()
        //comprimir imagen
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream)
        val bytes = outputStream.toByteArray()
        fotoBase64 = Base64.encodeToString(bytes, Base64.DEFAULT)
    }

    //enviar a back
    fun enviarReporte() {
        viewModelScope.launch {
            try {
                val nuevoReporte = Reporte(
                    titulo = titulo,
                    descripcion = descripcion,
                    latitud = latitud,
                    longitud = longitud,
                    imagenBase64 = fotoBase64
                )
                api.enviarReporte(nuevoReporte)
                limpiarFormulario()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // lista reportes
    fun cargarReportes() {
        viewModelScope.launch {
            try {
                val resultados = api.obtenerReportes()
                listaReportes.clear()
                listaReportes.addAll(resultados)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun limpiarFormulario() {
        titulo = ""
        descripcion = ""
        ubicacionText = "Ubicación no obtenida"
        fotoBase64 = ""
    }
}