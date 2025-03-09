package com.example.divisa.data

import android.database.Cursor
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.divisa.model.Divisa

@Dao
interface DivisaDao {
    @Insert
    suspend fun insertarDivisas(divisas: List<Divisa>)

    @Query("SELECT * FROM divisas WHERE fecha = :fecha")
    suspend fun obtenerDivisasPorFecha(fecha: String): List<Divisa>

    @Query("SELECT * FROM divisas WHERE fecha = :fecha")
    fun obtenerDivisasPorFechaCursor(fecha: String): Cursor

    @Query("SELECT * FROM divisas WHERE moneda = :moneda ORDER BY fecha")
    suspend fun obtenerHistorialDivisa(moneda: String): List<Divisa>

    @Query("SELECT * FROM divisas WHERE moneda = :moneda ORDER BY fecha")
     fun obtenerHistorialDivisaCursor(moneda: String): Cursor


}