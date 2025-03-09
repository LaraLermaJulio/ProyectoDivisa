package com.example.divisa.ui.screens

import android.app.Application
import android.database.Cursor
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.divisa.DivisaApplication
import com.example.divisa.data.DivisaRepository
import com.example.divisa.model.Divisa
import com.example.divisa.ui.components.PuntoGrafica
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

/**
 * UI state para la pantalla de divisas
 */
data class DivisaUiState(
    val divisas: List<Divisa> = emptyList(),
    val historialDivisas: List<PuntoGrafica> = emptyList(),
    val isLoading: Boolean = false,
    val error: String = ""
)

class DivisaViewModel(
    application: Application,
    private val divisaRepository: DivisaRepository
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(DivisaUiState())
    val uiState: StateFlow<DivisaUiState> = _uiState.asStateFlow()

    init {
        cargarDivisasPorFecha(obtenerFechaActual())
    }

    fun obtenerFechaActual(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }

    fun cargarDivisasPorFecha(fecha: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = "") }
            try {
                val divisas = withContext(Dispatchers.IO) {
                    cargarDivisasDesdeContentProvider(fecha)
                }

                Log.d("DivisaViewModel", "Divisas obtenidas desde ContentProvider: $divisas")

                // ✅ Solo sincronizar si realmente no hay datos en la BD
                if (divisas.isNotEmpty()) {
                    _uiState.update { it.copy(divisas = divisas, isLoading = false) }
                } else {
                    Log.d("DivisaViewModel", "No hay divisas en BD, sincronizando...")
                    sincronizarDivisas()
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Error al cargar divisas: ${e.message}", isLoading = false) }
            }
        }
    }


    fun cargarHistorialDivisas(monedaBase: String, monedaDestino: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = "") }
            try {
                // En una implementación real, obtendríamos datos históricos
                // Para este ejemplo, generamos datos simulados
                val datosHistoricos = generarDatosHistoricos(monedaBase, monedaDestino)
                _uiState.update {
                    it.copy(
                        historialDivisas = datosHistoricos,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        error = "Error al cargar historial: ${e.message}",
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun sincronizarDivisas() {
        viewModelScope.launch {
            try {
                divisaRepository.sincronizarDivisas()
                // Recargar datos después de sincronizar
                cargarDivisasPorFecha(obtenerFechaActual())
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        error = "Error al sincronizar divisas: ${e.message}",
                        isLoading = false
                    )
                }
            }
        }
    }

    fun cargarDivisasDesdeContentProvider(fecha: String): List<Divisa> {
        val listaDivisas = mutableListOf<Divisa>()
        val uri = Uri.parse("content://com.example.divisa.provider/divisas")
        val cursor: Cursor? = null

        cursor?.use {
            Log.d("ContentProvider", "Filas en Cursor: ${it.count}") // ✅ Verifica si hay datos
            while (it.moveToNext()) {
                val moneda = it.getString(it.getColumnIndexOrThrow("moneda"))
                val valor = it.getString(it.getColumnIndexOrThrow("valor"))
                val fechaDivisa = it.getString(it.getColumnIndexOrThrow("fecha"))

                Log.d("ContentProvider", "Moneda: $moneda, Valor: $valor, Fecha: $fechaDivisa") // ✅ Verifica cada fila

                listaDivisas.add(Divisa(null, moneda, valor, fechaDivisa))
            }
        }

        return listaDivisas
    }


    private fun generarDatosHistoricos(monedaBase: String, monedaDestino: String): List<PuntoGrafica> {
        // Esta función simula datos históricos para la gráfica
        // En una aplicación real, obtendrías estos datos del ContentProvider o repositorio

        val fechaActual = Calendar.getInstance()
        val formatoFecha = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val resultado = mutableListOf<PuntoGrafica>()

        // Obtener tasa actual para usarla como base
        val divisas = _uiState.value.divisas
        val tasaBase = divisas.find { it.moneda == monedaBase }?.valor?.toDoubleOrNull() ?: 1.0
        val tasaDestino = divisas.find { it.moneda == monedaDestino }?.valor?.toDoubleOrNull() ?: 1.0

        val tasaActual = if (tasaBase != 0.0) {
            tasaDestino / tasaBase
        } else {
            0.0
        }



        // Generar datos para los últimos 14 días
        fechaActual.add(Calendar.DAY_OF_MONTH, -14)
        for (i in 0 until 14) {
            val fecha = formatoFecha.format(fechaActual.time)

            // Simular fluctuación en la tasa (±5% respecto a la tasa actual)
            val variacion = (Math.random() * 0.1) - 0.05
            val valor = tasaActual * (1 + variacion)

            resultado.add(PuntoGrafica(fecha, valor))
            fechaActual.add(Calendar.DAY_OF_MONTH, 1)
        }

        // Añadir tasa actual
        resultado.add(PuntoGrafica(formatoFecha.format(Date()), tasaActual))

        return resultado
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as DivisaApplication)
                val divisaRepository = application.container.divisaRepository
                DivisaViewModel(
                    application = application,
                    divisaRepository = divisaRepository
                )
            }
        }
    }
}