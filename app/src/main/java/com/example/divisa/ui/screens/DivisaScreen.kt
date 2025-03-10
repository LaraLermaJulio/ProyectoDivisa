package com.example.divisa.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.divisa.data.SincronizacionEstado
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Pantalla principal para mostrar las divisas
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DivisaScreen(viewModel: DivisaViewModel) {
    // Recolectar estados
    val divisasUiState by viewModel.divisasUI.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val ultimaActualizacion by viewModel.ultimaActualizacion.collectAsState()
    val estadoSincronizacion by viewModel.estadoSincronizacion.collectAsState()
    val fechaSeleccionada by viewModel.fechaSeleccionada.collectAsState()
    val fechasDisponibles by viewModel.fechasDisponibles.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Divisas") },
                actions = {
                    IconButton(onClick = { viewModel.actualizarDivisas() }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Actualizar"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Selector de fecha
            FechaSelectorUI(
                fechaSeleccionada = fechaSeleccionada,
                fechasDisponibles = fechasDisponibles,
                onFechaSelected = { viewModel.seleccionarFecha(it) }
            )

            // Mostrar estado de sincronización
            when (estadoSincronizacion) {
                SincronizacionEstado.SINCRONIZANDO -> {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    Text(
                        text = "Sincronizando divisas...",
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
                SincronizacionEstado.EXITO -> {
                    if (ultimaActualizacion.isNotEmpty()) {
                        Text(
                            text = "Última actualización: ${formatearFechaHora(ultimaActualizacion)}",
                            style = MaterialTheme.typography.labelMedium,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                }
                SincronizacionEstado.ERROR -> {
                    Text(
                        text = "Error al sincronizar. Intente nuevamente.",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
                else -> { /* No mostrar nada */ }
            }

            // Mostrar contenido principal
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (divisasUiState.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "No hay divisas disponibles para esta fecha",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        OutlinedButton(onClick = { viewModel.actualizarDivisas() }) {
                            Text("Actualizar ahora")
                        }
                    }
                }
            } else {
                LazyColumn {
                    items(divisasUiState) { divisa ->
                        DivisaItem(divisa = divisa)
                    }
                }
            }
        }
    }
}

/**
 * Componente para mostrar un item de divisa
 */
@Composable
fun DivisaItem(divisa: DivisaUiState) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = divisa.moneda,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "1 MXN",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = divisa.valor,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

/**
 * Componente para selector de fecha
 */
@Composable
fun FechaSelectorUI(
    fechaSeleccionada: String,
    fechasDisponibles: List<String>,
    onFechaSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (fechaSeleccionada.isNotEmpty())
                    "Fecha: ${formatearFecha(fechaSeleccionada)}"
                else
                    "Seleccionar fecha",
                style = MaterialTheme.typography.titleMedium
            )
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = "Seleccionar fecha"
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth(0.9f)
        ) {
            if (fechasDisponibles.isEmpty()) {
                DropdownMenuItem(
                    text = { Text("No hay fechas disponibles") },
                    onClick = { expanded = false }
                )
            } else {
                fechasDisponibles.forEach { fecha ->
                    DropdownMenuItem(
                        text = { Text(formatearFecha(fecha)) },
                        onClick = {
                            onFechaSelected(fecha)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

/**
 * Función para formatear la fecha a un formato más legible
 */
private fun formatearFecha(fecha: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd MMM yyyy", Locale("es", "MX"))
        val date = inputFormat.parse(fecha.split(" ")[0])
        outputFormat.format(date!!)
    } catch (e: Exception) {
        fecha
    }
}

/**
 * Función para formatear fecha y hora a un formato más legible
 */
private fun formatearFechaHora(fechaHora: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd MMM yyyy HH:mm", Locale("es", "MX"))
        val date = inputFormat.parse(fechaHora)
        outputFormat.format(date!!)
    } catch (e: Exception) {
        fechaHora
    }
}