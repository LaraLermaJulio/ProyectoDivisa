package com.example.divisa.data

import android.database.Cursor
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.divisa.model.Divisa
import kotlinx.coroutines.flow.Flow

@Dao
interface DivisaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarDivisas(divisas: List<Divisa>)

    @Query("SELECT * FROM divisas WHERE fecha = :fecha")
    fun obtenerDivisasPorFechaFlow(fecha: String): Flow<List<Divisa>>

    @Query("SELECT * FROM divisas WHERE fecha = :fecha")
    suspend fun obtenerDivisasPorFecha(fecha: String): List<Divisa>

    @Query("SELECT * FROM divisas WHERE fecha = :fecha")
    fun obtenerDivisasPorFechaCursor(fecha: String): Cursor

    @Query("SELECT * FROM divisas WHERE moneda = :moneda ORDER BY fecha")
    fun obtenerHistorialDivisaFlow(moneda: String): Flow<List<Divisa>>

    @Query("SELECT * FROM divisas WHERE moneda = :moneda ORDER BY fecha")
    suspend fun obtenerHistorialDivisa(moneda: String): List<Divisa>

    @Query("SELECT * FROM divisas WHERE moneda = :moneda ORDER BY fecha")
    fun obtenerHistorialDivisaCursor(moneda: String): Cursor

    @Query("SELECT DISTINCT fecha FROM divisas ORDER BY fecha DESC")
    fun obtenerFechasDisponiblesFlow(): Flow<List<String>>
}