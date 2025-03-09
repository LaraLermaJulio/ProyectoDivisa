package com.example.divisa.ui.screens

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.divisa.DivisaApplication
import com.example.divisa.model.Divisa
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DivisaViewModel(application: Application) : AndroidViewModel(application) {

    var divisasPorRango by mutableStateOf<List<Divisa>>(emptyList())
        private set

    fun cargarDivisasPorRango(currency: String, startDate: String, endDate: String) {
        viewModelScope.launch {
            val uri = Uri.parse("content://com.example.divisa.provider/divisas").buildUpon()
                .appendQueryParameter("currency", currency)
                .appendQueryParameter("startDate", startDate)
                .appendQueryParameter("endDate", endDate)
                .build()

            divisasPorRango = withContext(Dispatchers.IO) {
                val cursor = getApplication<Application>().contentResolver.query(uri, null, null, null, null)
                cursor?.use {
                    val resultList = mutableListOf<Divisa>()
                    while (it.moveToNext()) {
                        val id = it.getLong(it.getColumnIndexOrThrow("id"))
                        val moneda = it.getString(it.getColumnIndexOrThrow("moneda"))
                        val tasa = it.getDouble(it.getColumnIndexOrThrow("tasa"))
                        val fechaHora = it.getString(it.getColumnIndexOrThrow("fechaHora"))
                        resultList.add(Divisa(id = id, moneda = moneda, tasa = tasa, fechaHora = fechaHora))
                    }
                    resultList
                } ?: emptyList()
            }

            Log.d("DivisaViewModel", "✅ Obtenidas ${divisasPorRango.size} divisas para $currency desde ContentProvider.")
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as DivisaApplication
                DivisaViewModel(app)
            }
        }
    }
}
