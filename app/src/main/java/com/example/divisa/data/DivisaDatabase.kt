package com.example.divisa.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.divisa.model.Divisa

@Database(entities = [Divisa::class], version = 1, exportSchema = false)
abstract class DivisaDatabase : RoomDatabase() {
    abstract fun divisaDao(): DivisaDao

    companion object {
        @Volatile
        private var INSTANCE: DivisaDatabase? = null

        fun getInstance(context: Context): DivisaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DivisaDatabase::class.java,
                    "divisa_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
