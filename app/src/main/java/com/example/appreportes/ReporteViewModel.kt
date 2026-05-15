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
    var titulo by mutableStateOf("")
    var descripcion by mutableStateOf("")
    var ubicacionText by mutableStateOf("Ubicación no obtenida")
    var latitud by mutableDoubleStateOf(0.0)
    var longitud by mutableDoubleStateOf(0.0)
    var fotoBase64 by mutableStateOf("")

    // Configuración de Retrofit (Requisito 5)
    private val retrofit = Retrofit.Builder()
        .baseUrl("http://10.0.2.2:3000/") // IP para el emulador hacia tu PC local
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val api = retrofit.create(ReporteApi::class.java)

    fun convertirImagenABase64(bitmap: Bitmap) {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream)
        val byteStep = outputStream.toByteArray()
        fotoBase64 = Base64.encodeToString(byteStep, Base64.DEFAULT)
    }

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
                // Aquí podrías limpiar los campos tras enviar
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}