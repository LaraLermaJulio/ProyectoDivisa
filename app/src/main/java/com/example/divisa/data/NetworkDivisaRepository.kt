package com.example.divisa.data

import android.util.Log
import com.example.divisa.model.Divisa
import com.example.divisa.network.DivisaApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

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

            Log.d("NetworkDivisaRepository", "✅ Sincronización exitosa en $fechaActual")
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

    /**
     * Retorna la fecha/hora actual en formato "EEE, dd MMM yyyy HH:mm:ss Z"
     * con la zona horaria local (ej: America/Mexico_City).
     */
    private fun obtenerFechaActual(): String {
        val displayFormat = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.getDefault()).apply {
            timeZone = TimeZone.getDefault()
        }
        return displayFormat.format(java.util.Date())
    }
}
