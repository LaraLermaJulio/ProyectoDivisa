package com.example.divisa

import android.app.Application
import androidx.work.Configuration
import com.example.divisa.data.AppContainer
import com.example.divisa.data.DefaultAppContainer

class DivisaApplication : Application(), Configuration.Provider {

    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        try {
            container = DefaultAppContainer(this)
            android.util.Log.d("DivisaApplication", "Contenedor inicializado correctamente")
        } catch (e: Exception) {
            android.util.Log.e("DivisaApplication", "Error al inicializar el contenedor", e)
        }
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setMinimumLoggingLevel(android.util.Log.DEBUG)
            .build()
}
