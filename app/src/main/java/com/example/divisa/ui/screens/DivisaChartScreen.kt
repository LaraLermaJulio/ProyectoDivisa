package com.example.divisa.ui.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * Composable para seleccionar fecha/hora en formato "EEE, dd MMM yyyy HH:mm:ss Z".
 */
@Composable
fun DateTimePickerField(
    label: String,
    dateTimeString: String,
    onDateTimeChange: (String) -> Unit
) {
    val context = LocalContext.current
    // Formato que se muestra en el TextField
    val displayFormat = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("America/Mexico_City")
    }

    // Calendar para manipular la fecha/hora actual
    val calendar = Calendar.getInstance().apply {
        timeZone = TimeZone.getTimeZone("America/Mexico_City")
        try {
            val parsedDate = displayFormat.parse(dateTimeString)
            if (parsedDate != null) {
                time = parsedDate
            }
        } catch (e: ParseException) {
            e.printStackTrace()
        }
    }

    Column {
        // Muestra la fecha/hora en un TextField de solo lectura
        TextField(
            value = dateTimeString,
            onValueChange = { /* No editar manualmente */ },
            label = { Text(label) },
            readOnly = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Botón para abrir DatePickerDialog y luego TimePickerDialog
        Button(onClick = {
            DatePickerDialog(
                context,
                { _, year, month, dayOfMonth ->
                    calendar.set(year, month, dayOfMonth)
                    // Luego abrimos TimePicker
                    TimePickerDialog(
                        context,
                        { _, hourOfDay, minute ->
                            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                            calendar.set(Calendar.MINUTE, minute)
                            calendar.set(Calendar.SECOND, 0)

                            val newDateTime = displayFormat.format(calendar.time)
                            onDateTimeChange(newDateTime)
                            Log.d("DateTimePickerField", "Nueva fecha/hora: $newDateTime")
                        },
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE),
                        true
                    ).show()
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }) {
            Text("Seleccionar $label")
        }
    }
}

/**
 * Pantalla que muestra:
 * - Moneda (TextField)
 * - Fechas "Desde" y "Hasta" (DateTimePickerField) en formato "EEE, dd MMM yyyy HH:mm:ss Z"
 * - Botón "Cargar Datos"
 * - Lista de Divisas invertidas (1 MXN -> USD)
 * - Gráfico con eje X basado en la fecha en milisegundos
 *   y con etiquetas intermedias aproximadas entre la fecha de inicio y la de fin.
 */
@Composable
fun DivisaChartScreen(viewModel: DivisaViewModel) {
    // Estado para la moneda
    var moneda by remember { mutableStateOf("USD") }

    // Estados para "Desde" y "Hasta" en formato "EEE, dd MMM yyyy HH:mm:ss Z"
    var fechaInicioDisplay by remember { mutableStateOf("Thu, 09 Jan 2025 20:19:00 -0600") }
    var fechaFinDisplay by remember { mutableStateOf("Sun, 09 Mar 2025 20:19:00 -0600") }

    // Formatos para la conversión
    val displayFormat = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.getDefault())
    val internalFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    Column(modifier = Modifier.padding(16.dp)) {

        // Campo de texto para la moneda
        TextField(
            value = moneda,
            onValueChange = { moneda = it },
            label = { Text("Moneda") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Campo "Desde"
        DateTimePickerField(
            label = "Desde",
            dateTimeString = fechaInicioDisplay,
            onDateTimeChange = { nuevaFecha ->
                fechaInicioDisplay = nuevaFecha
                Log.d("DivisaChartScreen", "Fecha inicio -> $nuevaFecha")
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Campo "Hasta"
        DateTimePickerField(
            label = "Hasta",
            dateTimeString = fechaFinDisplay,
            onDateTimeChange = { nuevaFecha ->
                fechaFinDisplay = nuevaFecha
                Log.d("DivisaChartScreen", "Fecha fin -> $nuevaFecha")
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Botón para cargar datos
        Button(onClick = {
            try {
                val inicioDate = displayFormat.parse(fechaInicioDisplay)
                val finDate = displayFormat.parse(fechaFinDisplay)

                val inicioStr = internalFormat.format(inicioDate)
                val finStr = internalFormat.format(finDate)

                Log.d("DivisaChartScreen", "Cargar Datos con: $inicioStr -> $finStr")
                viewModel.cargarDivisasPorRango(moneda, inicioStr, finStr)
            } catch (e: Exception) {
                Log.e("DivisaChartScreen", "Error parseando fechas: ${e.message}")
            }
        }) {
            Text("Cargar Datos")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Obtenemos la lista de divisas (invertidas)
        val listaDivisas = viewModel.divisasPorRango.value

        if (listaDivisas.isEmpty()) {
            Text("No hay resultados o no se ha cargado nada aún.")
        } else {
            // Mostrar la última invertida
            val ultimaDivisa = listaDivisas.last()
            val ultimaInversa = 1.0 / ultimaDivisa.tasa
            Text("1 MXN = %.6f %s".format(ultimaInversa, ultimaDivisa.moneda))

            Spacer(modifier = Modifier.height(16.dp))

            // Gráfico con MPAndroidChart
            AndroidView(
                factory = { context ->
                    LineChart(context).apply {
                        description.text = "Tipo de Cambio (1 MXN -> $moneda)"
                        xAxis.position = XAxis.XAxisPosition.BOTTOM
                        xAxis.granularity = 1f
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                update = { lineChart ->

                    // Convertimos cada divisa en un Entry con x=fechaEnMilis, y=1/tasa
                    val entries = listaDivisas.map { divisa ->
                        val date = internalFormat.parse(divisa.fechaHora) ?: Date()
                        val xValue = date.time.toFloat()
                        val yValue = (1f / divisa.tasa.toFloat())
                        Entry(xValue, yValue)
                    }.sortedBy { it.x }

                    val dataSet = LineDataSet(entries, "1 MXN -> $moneda").apply {
                        lineWidth = 2f
                        circleRadius = 3f
                        setDrawValues(false)
                        mode = LineDataSet.Mode.CUBIC_BEZIER
                    }

                    lineChart.data = LineData(dataSet)

                    // Forzamos el eje X a ir desde la fechaInicio hasta la fechaFin
                    // y mostrar varias etiquetas intermedias.
                    val startMillis = runCatching {
                        displayFormat.parse(fechaInicioDisplay)?.time
                    }.getOrNull() ?: entries.first().x.toLong()

                    val endMillis = runCatching {
                        displayFormat.parse(fechaFinDisplay)?.time
                    }.getOrNull() ?: entries.last().x.toLong()

                    lineChart.xAxis.axisMinimum = startMillis.toFloat()
                    lineChart.xAxis.axisMaximum = endMillis.toFloat()

                    // Elegimos cuántas etiquetas mostrar (aprox.)
                    lineChart.xAxis.setLabelCount(6, true)

                    // Definimos la granularidad en ms (por ejemplo, 10 días)
                    val tenDaysMs = 10 * 24 * 60 * 60 * 1000L
                    lineChart.xAxis.granularity = tenDaysMs.toFloat()
                    lineChart.xAxis.isGranularityEnabled = true

                    // Formatear el eje X como "MM-dd"
                    val chartDateFormat = SimpleDateFormat("MM-dd", Locale.getDefault())
                    lineChart.xAxis.valueFormatter = object : ValueFormatter() {
                        override fun getFormattedValue(value: Float): String {
                            return chartDateFormat.format(value.toLong())
                        }
                    }

                    lineChart.invalidate()
                }
            )
        }
    }
}
