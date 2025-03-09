package com.example.divisa.ui

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.divisa.ui.screens.DivisaViewModel

@Composable
fun DivisaApp() {
    val viewModel: DivisaViewModel = viewModel(factory = DivisaViewModel.Factory)

}
