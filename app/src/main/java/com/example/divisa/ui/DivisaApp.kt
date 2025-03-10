package com.example.divisa.ui

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.divisa.ui.screens.DivisaScreen
import com.example.divisa.ui.screens.DivisaViewModel

@Composable
fun DivisaApp() {
    val divisaViewModel: DivisaViewModel = viewModel(factory = DivisaViewModel.Factory)
    // Se lo pasas como parámetro a DivisaScreen
    DivisaScreen(viewModel = divisaViewModel)
}
