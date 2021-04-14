package com.example.currencyconverter.di

import com.example.currencyconverter.viewModel.CurrencyViewModel
import com.example.currencyconverter.ui.CurrencyActivity
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {
    fun inject(currencyViewModel: CurrencyViewModel)
}