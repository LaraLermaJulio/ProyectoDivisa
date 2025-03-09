package com.example.divisa.ui

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.divisa.ui.screens.DivisaViewModel
import com.example.divisa.ui.screens.DivisaScreen

@Composable
fun DivisaApp() {
    val divisaViewModel: DivisaViewModel =
        viewModel(factory = DivisaViewModel.Factory)

    DivisaScreen(viewModel = divisaViewModel)
}