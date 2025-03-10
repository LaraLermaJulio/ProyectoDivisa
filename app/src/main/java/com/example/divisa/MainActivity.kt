package com.example.divisa

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.divisa.ui.screens.DivisaChartScreen
import com.example.divisa.ui.screens.DivisaViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val viewModel: DivisaViewModel = viewModel(factory = DivisaViewModel.Factory)
            DivisaChartScreen(viewModel = viewModel)
        }
    }
}
