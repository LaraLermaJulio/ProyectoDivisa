package com.example.divisa.data

import android.util.Log
import com.example.divisa.model.Divisa
import com.example.divisa.network.DivisaApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface DivisaRepository {
    suspend fun sincronizarDivisas()
    suspend fun obtenerDivisasPorFecha(fecha: String): List<Divisa>
}

class NetworkDivisaRepository(
    private val divisaApiService: DivisaApiService,
    private val divisaDao: DivisaDao
) : DivisaRepository {

    override suspend fun sincronizarDivisas() {
        val respuesta = divisaApiService.getPrices()
        val fechaActual = obtenerFechaActual()

        val listaDivisas = respuesta.conversion_rates.map { (moneda, tasa) ->
            val divisa = Divisa(
                moneda = moneda,
                valor = tasa,
                fecha = fechaActual
            )
            Log.d("DEBUG_FECHA", "Divisa: $divisa") // Imprime la fecha que se va a guardar
            divisa
        }

        withContext(Dispatchers.IO) {
            divisaDao.insertarDivisas(listaDivisas)
        }
    }

    override suspend fun obtenerDivisasPorFecha(fecha: String): List<Divisa> {
        return withContext(Dispatchers.IO) {
            divisaDao.obtenerDivisasPorFecha(fecha)
        }
    }

    private fun obtenerFechaActual(): String {
        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault())
        sdf.timeZone = java.util.TimeZone.getTimeZone("America/Mexico_City")
        return sdf.format(java.util.Date())
    }
}
