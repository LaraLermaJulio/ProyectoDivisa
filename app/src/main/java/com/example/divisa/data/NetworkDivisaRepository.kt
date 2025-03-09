package com.example.divisa.data

import android.util.Log
import com.example.divisa.model.Divisa
import com.example.divisa.network.DivisaApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Locale

class NetworkDivisaRepository(
    private val divisaApiService: DivisaApiService,
    private val divisaDao: DivisaDao
) : DivisaRepository {

    override suspend fun sincronizarDivisas() {
        try {
            val respuesta = divisaApiService.getPrices()
            val fechaActual = obtenerFechaActual()

            val listaDivisas = respuesta.conversion_rates.map { (moneda, tasa) ->
                Divisa(moneda = moneda, tasa = tasa, fechaHora = fechaActual)
            }

            withContext(Dispatchers.IO) {
                divisaDao.insertarDivisas(listaDivisas)
            }

            Log.d("NetworkDivisaRepository", "✅ Sincronización exitosa, divisas insertadas.")
        } catch (e: Exception) {
            Log.e("NetworkDivisaRepository", "❌ Error al sincronizar divisas:", e)
        }
    }

    override suspend fun obtenerDivisasPorFecha(fecha: String): List<Divisa> {
        return withContext(Dispatchers.IO) {
            divisaDao.obtenerDivisasPorFecha(fecha)
        }
    }

    override suspend fun obtenerDivisasPorRango(
        currency: String,
        startDate: String,
        endDate: String
    ): List<Divisa> {
        return withContext(Dispatchers.IO) {
            divisaDao.obtenerDivisasPorRango(currency, startDate, endDate)
        }
    }

    private fun obtenerFechaActual(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        sdf.timeZone = java.util.TimeZone.getTimeZone("America/Mexico_City")
        return sdf.format(java.util.Date())
    }
}
