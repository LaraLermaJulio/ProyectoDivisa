package com.example.divisa.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

@Composable
fun DivisaScreen(viewModel: DivisaViewModel) {
    var moneda by remember { mutableStateOf("USD") }
    var fechaInicio by remember { mutableStateOf("2025-03-08 00:00:00") }
    var fechaFin by remember { mutableStateOf("2025-03-08 23:59:59") }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        TextField(
            value = moneda,
            onValueChange = { moneda = it },
            label = { Text("Moneda") }
        )

        TextField(
            value = fechaInicio,
            onValueChange = { fechaInicio = it },
            label = { Text("Fecha/Hora Inicio") }
        )

        TextField(
            value = fechaFin,
            onValueChange = { fechaFin = it },
            label = { Text("Fecha/Hora Fin") }
        )

        Button(onClick = {
            viewModel.cargarDivisasPorRango(moneda, fechaInicio, fechaFin)
        }) {
            Text("Cargar Datos")
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (viewModel.divisasPorRango.isEmpty()) {
            Text("No hay resultados o no se ha cargado nada aún.")
        } else {
            LazyColumn {
                items(viewModel.divisasPorRango) { divisa ->
                    Text("1 MXN = ${divisa.tasa} ${divisa.moneda} - ${divisa.fechaHora}")
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            AndroidView(factory = { context ->
                LineChart(context).apply {
                    description.text = "Tipo de Cambio MXN - $moneda"
                    xAxis.position = XAxis.XAxisPosition.BOTTOM
                }
            },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                update = { lineChart ->
                    val entries = viewModel.divisasPorRango.mapIndexed { index, divisa ->
                        Entry(index.toFloat(), divisa.tasa.toFloat())
                    }

                    val dataSet = LineDataSet(entries, "1 MXN → $moneda").apply {
                        lineWidth = 3f
                        circleRadius = 4f
                    }

                    lineChart.data = LineData(dataSet)
                    lineChart.notifyDataSetChanged()
                    lineChart.invalidate()
                })
        }
    }
}
