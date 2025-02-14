package com.example.marsphotos.ui

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.marsphotos.ui.screens.DivisaViewModel

@Composable
fun DivisaApp() {
    val divisaViewModel: DivisaViewModel =
        viewModel(factory = DivisaViewModel.Factory)
}