package com.example.marsphotos.ui

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.marsphotos.ui.screens.DivisaViewModel

@Composable
fun DivisaApp() {
    // Instancia del ViewModel, ejecuta automáticamente la sincronización
    val divisaViewModel: DivisaViewModel =
        viewModel(factory = DivisaViewModel.Factory)

    // Si querés volver a forzar la sincronización manualmente, podés hacerlo así:
    // divisaViewModel.sincronizarDivisas()
}