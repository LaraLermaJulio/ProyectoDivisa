package com.example.marsphotos.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.marsphotos.model.Divisa

@Dao
interface DivisaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarDivisas(divisas: List<Divisa>)

    @Query("SELECT * FROM divisas WHERE fecha = :fecha")
    suspend fun obtenerDivisasPorFecha(fecha: String): List<Divisa>
}
