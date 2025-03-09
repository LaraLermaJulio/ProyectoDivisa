package com.example.divisa.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "divisas")
data class Divisa(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val moneda: String,
    val valor: Double,
    val fecha: String
)
