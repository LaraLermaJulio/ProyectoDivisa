package com.example.divisa.ui.screens

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.divisa.model.Divisa
import com.example.divisa.ui.components.GraficaTipoCambio
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DivisaScreen(viewModel: DivisaViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedDateState = remember { mutableStateOf(viewModel.obtenerFechaActual()) }
    val monedaBase = remember { mutableStateOf("MXN") }
    val monedaDestino = remember { mutableStateOf("USD") }
    val showDatePicker = remember { mutableStateOf(false) }

    val divisas = uiState.divisas
    val disponibleDivisas = remember(divisas) {
        if (divisas.isNotEmpty()) {
            divisas.map { it.moneda }.distinct().sorted()
        } else {
            listOf("MXN", "USD", "EUR", "JPY", "GBP")
        }
    }

    // Cargar datos cada vez que cambia la fecha
    LaunchedEffect(selectedDateState.value) {
        viewModel.cargarDivisasPorFecha(selectedDateState.value)
    }

    // Cargar datos históricos cuando cambian las monedas seleccionadas
    LaunchedEffect(monedaBase.value, monedaDestino.value) {
        viewModel.cargarHistorialDivisas(monedaBase.value, monedaDestino.value)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Conversor de Divisas") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Selector de fecha
            OutlinedButton(
                onClick = { showDatePicker.value = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Fecha: ${selectedDateState.value}")
            }

            if (showDatePicker.value) {
                DatePickerDialog(
                    selectedDateState = selectedDateState,
                    onDismiss = { showDatePicker.value = false }
                )
            }

            // Selectores de divisas
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Selector de moneda base
                DivisaSelector(
                    label = "Moneda Base",
                    opciones = disponibleDivisas,
                    seleccionada = monedaBase.value,
                    onSeleccionCambiada = { monedaBase.value = it },
                    modifier = Modifier.weight(1f)
                )

                // Selector de moneda destino
                DivisaSelector(
                    label = "Moneda Destino",
                    opciones = disponibleDivisas,
                    seleccionada = monedaDestino.value,
                    onSeleccionCambiada = { monedaDestino.value = it },
                    modifier = Modifier.weight(1f)
                )
            }

            // Mostrar tasa de cambio actual
            TasaDeCambioActual(
                divisas = divisas,
                monedaBase = monedaBase.value,
                monedaDestino = monedaDestino.value
            )

            // Gráfica
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(320.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Text(
                        "Variación del tipo de cambio",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    GraficaTipoCambio(
                        viewModel = viewModel,
                        monedaBase = monedaBase.value,
                        monedaDestino = monedaDestino.value,
                        datosHistoricos = uiState.historialDivisas,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    )
                }
            }

            // Estado de carga
            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            // Mensajes de error
            if (uiState.error.isNotEmpty()) {
                Text(
                    text = uiState.error,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@SuppressLint("DefaultLocale")
@Composable
fun TasaDeCambioActual(
    divisas: List<Divisa>,
    monedaBase: String,
    monedaDestino: String
) {
    val tasaBase = divisas.find { it.moneda == monedaBase }?.valor?.toDoubleOrNull() ?: 1.0
    val tasaDestino = divisas.find { it.moneda == monedaDestino }?.valor?.toDoubleOrNull() ?: 1.0
    val tasaCambio = if (tasaBase != 0.0) tasaDestino / tasaBase else 0.0

    Log.d("DivisaViewModel", "Nueva tasaBase: $tasaBase, tasaDestino: $tasaDestino, tasaCambio: $tasaCambio")

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Tasa de Cambio Actual",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "1 $monedaBase = ${String.format("%.4f", tasaCambio)} $monedaDestino",
                style = MaterialTheme.typography.headlineMedium
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DivisaSelector(
    label: String,
    opciones: List<String>,
    seleccionada: String,
    onSeleccionCambiada: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium
        )

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it }
        ) {
            OutlinedTextField(
                value = seleccionada,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                opciones.forEach { opcion ->
                    DropdownMenuItem(
                        text = { Text(opcion) },
                        onClick = {
                            onSeleccionCambiada(opcion)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialog(
    selectedDateState: MutableState<String>,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState()

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        selectedDateState.value = sdf.format(Date(millis))
                    }
                    onDismiss()
                }
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}