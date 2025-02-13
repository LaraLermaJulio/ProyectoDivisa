package com.example.marsphotos.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "divisas")
data class Divisa(
    @PrimaryKey val moneda: String,
    val valor: Double,
    val fecha: String
)
