package com.example.divisa

import android.app.Application
import com.example.divisa.data.AppContainer
import com.example.divisa.data.DefaultAppContainer

class DivisaApplication : Application() {
    lateinit var container: AppContainer
    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
    }
}
