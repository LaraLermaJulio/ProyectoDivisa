package com.example.marsphotos.data

import com.example.marsphotos.model.Divisa
import com.example.marsphotos.network.DivisaApiService
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

        val listaDivisas = respuesta.rates.map { (moneda, tasa) ->
            Divisa(moneda, tasa, fechaActual)
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
        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
        return sdf.format(java.util.Date())
    }
}