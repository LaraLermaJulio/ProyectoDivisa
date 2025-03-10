package com.example.divisa.ui.screens

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.divisa.DivisaApplication
import com.example.divisa.data.DivisaRepository
import com.example.divisa.data.SincronizacionEstado
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * Estado de UI para representar una divisa en la pantalla
 */
data class DivisaUiState(
    val moneda: String,
    val valor: String,
    val fecha: String
)

/**
 * ViewModel para manejar la lógica de la pantalla de divisas
 */
class DivisaViewModel(private val divisaRepository: DivisaRepository) : ViewModel() {

    // Estado para la fecha seleccionada
    private val _fechaSeleccionada = MutableStateFlow(obtenerFechaActual())
    val fechaSeleccionada: StateFlow<String> = _fechaSeleccionada

    // Estados expuestos del repositorio
    val estadoSincronizacion = divisaRepository.estadoSincronizacion
    val ultimaActualizacion = divisaRepository.ultimaActualizacion

    // Lista de divisas para mostrar en la UI
    private val _divisasUI = MutableStateFlow<List<DivisaUiState>>(emptyList())
    val divisasUI: StateFlow<List<DivisaUiState>> = _divisasUI

    // Estado de carga
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // Estados para el selector de fechas
    private val _fechasDisponibles = MutableStateFlow<List<String>>(emptyList())
    val fechasDisponibles: StateFlow<List<String>> = _fechasDisponibles

    init {
        // Observar cambios en la fecha seleccionada y cargar divisas
        viewModelScope.launch {
            fechaSeleccionada.collectLatest { fecha ->
                cargarDivisasPorFecha(fecha)
            }
        }


    }

    /**
     * Carga las divisas para una fecha específica
     */
    private suspend fun cargarDivisasPorFecha(fecha: String) {
        _isLoading.value = true
        try {
            divisaRepository.obtenerDivisasPorFechaFlow(fecha).collectLatest { divisas ->
                _divisasUI.value = divisas.map { divisa ->
                    DivisaUiState(
                        moneda = divisa.moneda,
                        valor = divisa.valor,
                        fecha = divisa.fecha
                    )
                }
                _isLoading.value = false
            }
        } catch (e: Exception) {
            _isLoading.value = false
            Log.e("DivisaViewModel", "Error al cargar divisas: ${e.message}")
        }
    }

    /**
     * Selecciona una nueva fecha para mostrar
     */
    fun seleccionarFecha(fecha: String) {
        _fechaSeleccionada.value = fecha
    }

    /**
     * Realiza una sincronización manual de divisas
     */
    fun actualizarDivisas() {
        viewModelScope.launch {
            try {
                divisaRepository.sincronizarDivisas()
            } catch (e: Exception) {
                Log.e("DivisaViewModel", "Error al actualizar divisas: ${e.message}")
            }
        }
    }

    /**
     * Obtiene la fecha actual en formato YYYY-MM-DD
     */
    private fun obtenerFechaActual(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        sdf.timeZone = TimeZone.getTimeZone("America/Mexico_City")
        return sdf.format(Date())
    }

    /**
     * Factory para crear instancias del ViewModel
     */
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as DivisaApplication)
                val divisaRepository = application.container.divisaRepository
                DivisaViewModel(divisaRepository = divisaRepository)
            }
        }
    }
}