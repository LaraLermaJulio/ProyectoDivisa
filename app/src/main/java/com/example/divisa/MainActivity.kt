package com.example.divisa.ui

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.divisa.ui.theme.DivisaTheme
import com.example.divisa.workers.ActualizarDivisasWorker
import java.util.concurrent.TimeUnit


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("MainActivity", "onCreate se ejecutó correctamente")
        programarActualizacionDivisas()

        setContent {
            DivisaTheme {
                DivisaApp()
            }
        }
    }

    private fun programarActualizacionDivisas() {
        // Usar una frecuencia más apropiada para la batería (cada 6 horas)
        val trabajoRepetido = PeriodicWorkRequestBuilder<ActualizarDivisasWorker>(
            6, TimeUnit.HOURS
        )
            .setInitialDelay(10, TimeUnit.MINUTES) // Dar tiempo para que la app se inicie completamente
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "actualizarDivisas",
            ExistingPeriodicWorkPolicy.KEEP,
            trabajoRepetido
        )

        // Opcionalmente, ejecutar una sincronización inmediata al iniciar
        val trabajoInmediato = OneTimeWorkRequestBuilder<ActualizarDivisasWorker>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()

        WorkManager.getInstance(this).enqueue(trabajoInmediato)
    }
}