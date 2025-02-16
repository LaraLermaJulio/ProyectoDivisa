package com.example.divisa.ui

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.work.ExistingPeriodicWorkPolicy
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

        val trabajoRepetido = PeriodicWorkRequestBuilder<ActualizarDivisasWorker>(
            15, TimeUnit.MINUTES
        )
            .setInitialDelay(0, TimeUnit.MINUTES)
            .build()


        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "actualizarDivisas",
            ExistingPeriodicWorkPolicy.KEEP,
            trabajoRepetido
        )
    }
}