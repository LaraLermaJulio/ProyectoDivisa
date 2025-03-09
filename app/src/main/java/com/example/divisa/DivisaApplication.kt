package com.example.divisa

import android.app.Application
import com.example.divisa.data.AppContainer
import com.example.divisa.data.DefaultAppContainer

class DivisaApplication : Application() {
    val container: AppContainer by lazy { DefaultAppContainer(this) }

    override fun onCreate() {
        super.onCreate()
    }
}

