package com.example.currencyconverter

import android.app.Application
import com.example.currencyconverter.di.AppComponent
import com.example.currencyconverter.di.AppModule
import com.example.currencyconverter.di.DaggerAppComponent


class App : Application() {

    companion object {
        lateinit var instance: App private set
        lateinit var component: AppComponent private set
    }

    override fun onCreate() {
        super.onCreate()

        instance = this
        component = DaggerAppComponent.builder()
            .appModule(AppModule(this))
            .build()
    }
}