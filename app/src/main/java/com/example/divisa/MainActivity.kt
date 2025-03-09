package com.example.divisa

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.example.divisa.ui.screens.DivisaScreen
import com.example.divisa.ui.screens.DivisaViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: DivisaViewModel by viewModels { DivisaViewModel.Factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DivisaScreen(viewModel = viewModel)
        }
    }
}
