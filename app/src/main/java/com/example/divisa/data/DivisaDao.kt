package com.example.divisa.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.divisa.model.Divisa

@Dao
interface DivisaDao {
    @Insert
    suspend fun insertarDivisas(divisas: List<Divisa>)

    @Query("SELECT * FROM divisas WHERE fechaHora LIKE :fecha || '%'")
    suspend fun obtenerDivisasPorFecha(fecha: String): List<Divisa>

    @Query("""
        SELECT * FROM divisas
        WHERE moneda = :currency
          AND fechaHora BETWEEN :startDate AND :endDate
        ORDER BY fechaHora ASC
    """)
    suspend fun obtenerDivisasPorRango(
        currency: String,
        startDate: String,
        endDate: String
    ): List<Divisa>
}
