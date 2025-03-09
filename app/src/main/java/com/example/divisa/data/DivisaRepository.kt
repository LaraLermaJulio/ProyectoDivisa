package com.example.divisa.data

import com.example.divisa.model.Divisa

interface DivisaRepository {
    suspend fun sincronizarDivisas()
    suspend fun obtenerDivisasPorFecha(fecha: String): List<Divisa>
    suspend fun obtenerDivisasPorRango(currency: String, startDate: String, endDate: String): List<Divisa>


}



