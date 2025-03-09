package com.example.divisa.ui.components

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.divisa.ui.screens.DivisaViewModel
import java.text.SimpleDateFormat
import java.util.*

data class PuntoGrafica(
    val fecha: String,
    val valor: Double
)

@Composable
fun GraficaTipoCambio(
    viewModel: DivisaViewModel,
    monedaBase: String,
    monedaDestino: String,
    datosHistoricos: List<PuntoGrafica>,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        if (datosHistoricos.isNotEmpty()) {
            GraficaLineal(
                puntos = datosHistoricos,
                monedaBase = monedaBase,
                monedaDestino = monedaDestino,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Cargando datos históricos...",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
fun GraficaLineal(
    puntos: List<PuntoGrafica>,
    monedaBase: String,
    monedaDestino: String,
    modifier: Modifier = Modifier,
    colorLinea: Color = MaterialTheme.colorScheme.primary
) {
    if (puntos.isEmpty()) return

    val valoresY = puntos.map { it.valor }
    val minY = valoresY.minOrNull()?.let { it * 0.95 } ?: 0.0  // 5% menor que el mínimo
    val maxY = valoresY.maxOrNull()?.let { it * 1.05 } ?: 1.0  // 5% mayor que el máximo
    val rango = maxY - minY

    val etiquetaTextStyle = TextStyle(
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
        fontSize = 10.sp
    )

    Canvas(modifier = modifier.padding(8.dp)) {
        val alturaGrafica = size.height - 40f // Espacio para etiquetas
        val anchoGrafica = size.width - 60f  // Espacio para etiquetas Y

        val espaciadoX = if (puntos.size > 1) {
            anchoGrafica / (puntos.size - 1)
        } else {
            anchoGrafica
        }

        // Dibujar eje Y
        drawLine(
            color = Color.Gray.copy(alpha = 0.5f),
            start = Offset(60f, 0f),
            end = Offset(60f, alturaGrafica),
            strokeWidth = 1.5f
        )

        // Dibujar eje X
        drawLine(
            color = Color.Gray.copy(alpha = 0.5f),
            start = Offset(60f, alturaGrafica),
            end = Offset(size.width, alturaGrafica),
            strokeWidth = 1.5f
        )

        // Dibujar líneas horizontales de referencia
        val numLineasRef = 5
        val espacioEntreLineas = alturaGrafica / numLineasRef

        for (i in 0..numLineasRef) {
            val y = alturaGrafica - i * espacioEntreLineas

            // Dibujar línea
            drawLine(
                color = Color.Gray.copy(alpha = 0.2f),
                start = Offset(60f, y),
                end = Offset(size.width, y),
                strokeWidth = 0.5f
            )

            // Etiqueta de valor
            val valorEtiqueta = minY + (rango * i / numLineasRef)
            drawContext.canvas.nativeCanvas.drawText(
                String.format("%.4f", valorEtiqueta),
                30f,
                y + 5f,
                Paint().apply {
                    color = Color.Gray.toArgb()
                    textSize = 10.sp.toPx()
                    textAlign = Paint.Align.RIGHT
                }
            )
        }

        // Dibujar línea de la gráfica
        for (i in 0 until puntos.size - 1) {
            val startX = 60f + i * espaciadoX
            val startY = alturaGrafica - ((puntos[i].valor - minY) / rango * alturaGrafica).toFloat()

            val endX = 60f + (i + 1) * espaciadoX
            val endY = alturaGrafica - ((puntos[i + 1].valor - minY) / rango * alturaGrafica).toFloat()

            drawLine(
                color = colorLinea,
                start = Offset(startX, startY),
                end = Offset(endX, endY),
                strokeWidth = 2.5f
            )
        }

        // Dibujar puntos en la gráfica
        puntos.forEachIndexed { index, punto ->
            val x = 60f + index * espaciadoX
            val y = alturaGrafica - ((punto.valor - minY) / rango * alturaGrafica).toFloat()

            drawCircle(
                color = colorLinea,
                radius = 4f,
                center = Offset(x, y)
            )

            // Etiquetas de fecha (mostrar solo algunas para evitar sobreposición)
            if (index % ((puntos.size / 5).coerceAtLeast(1)) == 0) {
                drawContext.canvas.nativeCanvas.drawText(
                    formatearFecha(punto.fecha),
                    x,
                    alturaGrafica + 20f,
                    Paint().apply {
                        color = Color.Gray.toArgb()
                        textSize = 10.sp.toPx()
                        textAlign = Paint.Align.CENTER
                    }
                )
            }
        }

        // Dibujar título en la parte superior
        drawContext.canvas.nativeCanvas.drawText(
            "$monedaBase → $monedaDestino",
            size.width / 2,
            20f,
            Paint().apply {
                color = colorLinea.toArgb()
                textSize = 14.sp.toPx()
                textAlign = Paint.Align.CENTER
                isFakeBoldText = true
            }
        )
    }
}

// Función para formatear fechas
private fun formatearFecha(fecha: String): String {
    try {
        val formatoEntrada = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val formatoSalida = SimpleDateFormat("dd/MM", Locale.getDefault())
        val date = formatoEntrada.parse(fecha)
        return date?.let { formatoSalida.format(it) } ?: fecha
    } catch (e: Exception) {
        return fecha
    }
}