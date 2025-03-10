package com.example.divisa.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Pantalla para mostrar y cargar divisas según moneda y rango de fecha/hora,
 * usando la lógica de DivisaViewModel.
 */
@Composable
fun DivisaScreen(viewModel: DivisaViewModel) {
    // Estado para la moneda
    var moneda by remember { mutableStateOf("USD") }

    // Definimos formato para ejemplo
    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    // Fecha/hora actual
    val fechaActual = sdf.format(Date())
    // Hace un mes
    val calendario = Calendar.getInstance().apply {
        add(Calendar.MONTH, -1)
    }
    val fechaInicioPredeterminada = sdf.format(calendario.time)

    // Estados locales para “Desde” y “Hasta”
    var fechaInicio by remember { mutableStateOf(fechaInicioPredeterminada) }
    var fechaFin by remember { mutableStateOf(fechaActual) }

    // Obtenemos la lista de divisas desde el ViewModel (usa .value, no collectAsState)
    val listaDivisas = viewModel.divisasPorRango.value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        // Campo de texto para la moneda
        TextField(
            value = moneda,
            onValueChange = { moneda = it },
            label = { Text("Moneda") }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Campo para la fecha/hora de inicio
        TextField(
            value = fechaInicio,
            onValueChange = { fechaInicio = it },
            label = { Text("Desde") }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Botón que en un futuro podrías reemplazar con un DatePicker
        Button(onClick = {
            // Lógica para abrir DatePicker (si lo deseas)
        }) {
            Text("Seleccionar Desde")
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Campo para la fecha/hora de fin
        TextField(
            value = fechaFin,
            onValueChange = { fechaFin = it },
            label = { Text("Hasta") }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Botón que en un futuro podrías reemplazar con un DatePicker
        Button(onClick = {
            // Lógica para abrir DatePicker (si lo deseas)
        }) {
            Text("Seleccionar Hasta")
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Botón para sincronizar con la API
        Button(onClick = { viewModel.sincronizarDivisas() }) {
            Text("Actualizar Datos desde API")
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Botón para cargar datos del ContentProvider
        Button(onClick = {
            viewModel.cargarDivisasPorRango(moneda, fechaInicio, fechaFin)
        }) {
            Text("Cargar Datos")
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Verificamos si la lista está vacía
        if (listaDivisas.isEmpty()) {
            Text("No hay resultados o no se ha cargado nada aún.")
        } else {
            // Muestra la última divisa (ejemplo)
            val ultimaDivisa = listaDivisas.lastOrNull()
            ultimaDivisa?.let {
                // Ejemplo de mostrar la tasa invertida (1 MXN = X MONEDA)
                Text("1 MXN = ${String.format("%.6f", 1 / it.tasa)} ${it.moneda}")
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Lista de divisas
            LazyColumn {
                items(listaDivisas) { divisa ->
                    // Formatear la fecha de la divisa a algo más legible
                    val formatoEntrada = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                    val formatoSalida = SimpleDateFormat("dd 'de' MMMM 'de' yyyy - HH:mm", Locale.getDefault())

                    val fechaFormateada = try {
                        formatoSalida.format(formatoEntrada.parse(divisa.fechaHora)!!)
                    } catch (e: Exception) {
                        divisa.fechaHora // Si falla el parse, mostramos tal cual
                    }

                    Text(
                        text = "1 MXN = ${
                            String.format("%.6f", 1 / divisa.tasa)
                        } ${divisa.moneda} - $fechaFormateada"
                    )
                }
            }
        }
    }
}
