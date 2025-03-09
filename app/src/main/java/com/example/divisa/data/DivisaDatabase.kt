package com.example.divisa.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.divisa.model.Divisa

@Database(entities = [Divisa::class], version = 2, exportSchema = false)
abstract class DivisaDatabase : RoomDatabase() {
    abstract fun divisaDao(): DivisaDao
}