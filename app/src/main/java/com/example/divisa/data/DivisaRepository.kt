package com.example.divisa.data

import android.content.Context
import android.util.Log
import com.example.divisa.model.Divisa
import com.example.divisa.network.DivisaApiService
import com.example.divisa.provider.DivisaContentProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

interface DivisaRepository {
    suspend fun sincronizarDivisas()
    fun obtenerDivisasPorFechaFlow(fecha: String): Flow<List<Divisa>>
    suspend fun obtenerDivisasPorFecha(fecha: String): List<Divisa>
    val ultimaActualizacion: StateFlow<String>
    val estadoSincronizacion: StateFlow<SincronizacionEstado>
}

enum class SincronizacionEstado {
    INACTIVO, SINCRONIZANDO, EXITO, ERROR
}

class NetworkDivisaRepository(
    private val context: Context, // Se agrega el contexto aquí
    private val divisaApiService: DivisaApiService,
    private val divisaDao: DivisaDao
) : DivisaRepository {

    private val _ultimaActualizacion = MutableStateFlow("")
    override val ultimaActualizacion: StateFlow<String> = _ultimaActualizacion

    private val _estadoSincronizacion = MutableStateFlow(SincronizacionEstado.INACTIVO)
    override val estadoSincronizacion: StateFlow<SincronizacionEstado> = _estadoSincronizacion

    override suspend fun sincronizarDivisas() {
        try {
            _estadoSincronizacion.value = SincronizacionEstado.SINCRONIZANDO

            val respuesta = divisaApiService.getPrices()
            val fechaActual = obtenerFechaActual()
            _ultimaActualizacion.value = fechaActual

            val listaDivisas = respuesta.conversion_rates.map { (moneda, tasa) ->
                Divisa(moneda = moneda, valor = tasa.toString(), fecha = fechaActual)
            }

            withContext(Dispatchers.IO) {
                divisaDao.insertarDivisas(listaDivisas)
                Log.d("DivisaRepository", "Divisas sincronizadas: ${listaDivisas.size} registros")
            }

            // Notificar a los ContentObservers que los datos han cambiado
            context.contentResolver.notifyChange(DivisaContentProvider.CONTENT_URI, null)

            _estadoSincronizacion.value = SincronizacionEstado.EXITO
        } catch (e: Exception) {
            Log.e("DivisaRepository", "Error al sincronizar: ${e.message}")
            _estadoSincronizacion.value = SincronizacionEstado.ERROR
            throw e
        }
    }

    override fun obtenerDivisasPorFechaFlow(fecha: String): Flow<List<Divisa>> {
        return divisaDao.obtenerDivisasPorFechaFlow(fecha)
    }

    override suspend fun obtenerDivisasPorFecha(fecha: String): List<Divisa> {
        return withContext(Dispatchers.IO) {
            divisaDao.obtenerDivisasPorFecha(fecha)
        }
    }

    private fun obtenerFechaActual(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        sdf.timeZone = TimeZone.getTimeZone("America/Mexico_City")
        return sdf.format(Date())
    }
}
