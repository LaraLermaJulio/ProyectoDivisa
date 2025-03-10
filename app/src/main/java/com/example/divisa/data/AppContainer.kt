package com.example.divisa.data

import android.content.Context
import androidx.room.Room
import com.example.divisa.network.DivisaApiService
import retrofit2.Retrofit
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType

interface AppContainer {
    val divisaRepository: DivisaRepository
    val database: DivisaDatabase // Exponer la base de datos
}

class DefaultAppContainer(private val context: Context) : AppContainer {
    private val baseUrl = "https://v6.exchangerate-api.com/v6/1fb9eeb83af165e94771c0f8/"

    private val retrofit: Retrofit = Retrofit.Builder()
        .addConverterFactory(
            Json {
                ignoreUnknownKeys = true
            }.asConverterFactory("application/json".toMediaType())
        )
        .baseUrl(baseUrl)
        .build()

    private val retrofitService: DivisaApiService by lazy {
        retrofit.create(DivisaApiService::class.java)
    }

    override val database: DivisaDatabase by lazy {
        Room.databaseBuilder(
            context,
            DivisaDatabase::class.java,
            "divisa_db"
        ).fallbackToDestructiveMigration().build()
    }

    override val divisaRepository: DivisaRepository by lazy {
        NetworkDivisaRepository(context, retrofitService, database.divisaDao())
    }
}
