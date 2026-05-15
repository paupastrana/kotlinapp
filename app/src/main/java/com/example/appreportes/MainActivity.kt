package com.example.appreportes

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.android.gms.location.LocationServices

class MainActivity : ComponentActivity() {
    private val viewModel: ReporteViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        setContent {
            var mostrarLista by remember { mutableStateOf(false) }

            Surface(modifier = Modifier.fillMaxSize()) {
                Column {
                    //cambiar vist
                    Button(
                        onClick = { mostrarLista = !mostrarLista },
                        modifier = Modifier.padding(16.dp).fillMaxWidth()
                    ) {
                        Text(if (mostrarLista) "Ir a Crear Reporte" else "Ver Lista de Reportes")
                    }

                    if (mostrarLista) {
                        //lkista reportes
                        ListaReportesScreen(vm = viewModel)
                    } else {
                        // reporte crear
                        FormularioScreen(
                            vm = viewModel,
                            onGetLocation = {
                                try {
                                    fusedLocationClient.lastLocation.addOnSuccessListener { loc ->
                                        loc?.let {
                                            viewModel.latitud = it.latitude
                                            viewModel.longitud = it.longitude
                                            viewModel.ubicacionText = "${it.latitude}, ${it.longitude}"
                                        }
                                    }
                                } catch (e: SecurityException) { }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FormularioScreen(vm: ReporteViewModel, onGetLocation: () -> Unit) {
    //internet
    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { it?.let { vm.convertirImagenABase64(it) } }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { if (it[Manifest.permission.ACCESS_FINE_LOCATION] == true) onGetLocation() }

    Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Nuevo Reporte", style = MaterialTheme.typography.headlineMedium)

        OutlinedTextField(value = vm.titulo, onValueChange = { vm.titulo = it }, label = { Text("Título") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = vm.descripcion, onValueChange = { vm.descripcion = it }, label = { Text("Descripción") }, modifier = Modifier.fillMaxWidth())

        Text(vm.ubicacionText, modifier = Modifier.padding(8.dp))

        //imagen y gps
        Button(onClick = { cameraLauncher.launch(null) }) { Text("Tomar Foto") }
        Button(onClick = { permissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)) }) { Text("Obtener GPS") }

        Spacer(modifier = Modifier.weight(1f))

        //enviar a api
        Button(onClick = { vm.enviarReporte() }, modifier = Modifier.fillMaxWidth()) {
            Text("Enviar Reporte")
        }
    }
}

@Composable
fun ListaReportesScreen(vm: ReporteViewModel) {
    //datos
    LaunchedEffect(Unit) { vm.cargarReportes() }

    //reportes
    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        items(vm.listaReportes) { reporte ->
            Card(modifier = Modifier.padding(vertical = 8.dp).fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = reporte.titulo, style = MaterialTheme.typography.titleLarge)
                    Text(text = reporte.descripcion)
                    Text(
                        text = "Ubicación: ${reporte.latitud}, ${reporte.longitud}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}