package com.example.marsphotos.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.marsphotos.model.Divisa

@Database(entities = [Divisa::class], version = 1, exportSchema = false)
abstract class DivisaDatabase : RoomDatabase() {
    abstract fun divisaDao(): DivisaDao
}
