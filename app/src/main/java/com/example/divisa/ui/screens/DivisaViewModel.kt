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
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DivisaViewModel(private val divisaRepository: DivisaRepository) : ViewModel() {

    init {
        sincronizarDivisas()
    }

    fun sincronizarDivisas() {
        viewModelScope.launch {
            try {
                divisaRepository.sincronizarDivisas()
                Log.d("DivisaViewModel", "Sincronización exitosa.")
                obtenerDivisasDeHoy()
            } catch (e: IOException) {
                Log.e("DivisaViewModel", "Error de red: ${e.message}")
            } catch (e: HttpException) {
                Log.e("DivisaViewModel", "Error HTTP: ${e.message}")
            }
        }
    }

    fun obtenerDivisasDeHoy() {
        val fechaHoy = obtenerFechaActual()
        viewModelScope.launch {
            try {
                val divisasHoy = divisaRepository.obtenerDivisasPorFecha(fechaHoy)
                divisasHoy.forEach { divisa ->
                    Log.d(
                        "DivisaViewModel",
                        "Moneda: ${divisa.moneda}, Tasa: ${divisa.valor}, Fecha: ${divisa.fecha}"
                    )
                }
                if (divisasHoy.isEmpty()) {
                    Log.d("DivisaViewModel", "No se encontraron divisas para hoy.")
                }
            } catch (e: Exception) {
                Log.e("DivisaViewModel", "Error al obtener divisas: ${e.message}")
            }
        }
    }

    private fun obtenerFechaActual(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as DivisaApplication)
                val divisaRepository = application.container.divisaRepository
                DivisaViewModel(divisaRepository)
            }
        }
    }
}
