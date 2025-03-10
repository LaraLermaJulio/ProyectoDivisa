package com.example.divisa.ui.screens

import android.app.Application
import android.database.Cursor
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.divisa.DivisaApplication
import com.example.divisa.data.DivisaRepository
import com.example.divisa.model.Divisa
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

class DivisaViewModel(
    application: Application,
    private val divisaRepository: DivisaRepository
) : AndroidViewModel(application) {

    var divisasPorRango = mutableStateOf<List<Divisa>>(emptyList())
        private set

    init {
        sincronizarDivisas()
    }

    fun sincronizarDivisas() {
        viewModelScope.launch {
            try {
                divisaRepository.sincronizarDivisas()
                Log.d("DivisaViewModel", "Sincronización exitosa.")
                // Podrías llamar a obtenerDivisasDeHoy() si quieres
            } catch (e: IOException) {
                Log.e("DivisaViewModel", "Error de red: ${e.message}")
            } catch (e: HttpException) {
                Log.e("DivisaViewModel", "Error HTTP: ${e.message}")
            }
        }
    }

    fun cargarDivisasPorRango(currency: String, startDate: String, endDate: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    val baseUri = "content://com.example.divisa.provider/divisas"
                    val uri = Uri.parse(baseUri).buildUpon()
                        .appendQueryParameter("currency", currency)
                        .appendQueryParameter("startDate", startDate)
                        .appendQueryParameter("endDate", endDate)
                        .build()

                    val cursor: Cursor? = getApplication<Application>().contentResolver.query(uri, null, null, null, null)

                    val COLUMN_ID = "id"
                    val COLUMN_MONEDA = "moneda"
                    val COLUMN_TASA = "tasa"
                    val COLUMN_FECHA = "fechaHora"

                    val lista = mutableListOf<Divisa>()
                    cursor?.use {
                        while (it.moveToNext()) {
                            val id = it.getLong(it.getColumnIndexOrThrow(COLUMN_ID))
                            val moneda = it.getString(it.getColumnIndexOrThrow(COLUMN_MONEDA))
                            val tasa = it.getDouble(it.getColumnIndexOrThrow(COLUMN_TASA))
                            val fecha = it.getString(it.getColumnIndexOrThrow(COLUMN_FECHA))
                            lista.add(Divisa(id, moneda, tasa, fecha))
                        }
                    }
                    divisasPorRango.value = lista
                    Log.d("DivisaViewModel", "Cargadas ${lista.size} divisas del ContentProvider")
                } catch (e: Exception) {
                    Log.e("DivisaViewModel", "Error al cargar datos del ContentProvider: ${e.message}")
                }
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as DivisaApplication
                val repository = application.container.divisaRepository
                DivisaViewModel(application, repository)
            }
        }
    }
}
