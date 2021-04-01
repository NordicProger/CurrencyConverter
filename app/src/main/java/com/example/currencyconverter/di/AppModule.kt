package com.example.currencyconverter.di

import android.content.Context
import com.example.currencyconverter.contract.Contract
import com.example.currencyconverter.presenter.CurrencyPresenter
import com.example.currencyconverter.ui.CurrencyActivity
import com.example.model.currency.CurrencyInfo
import com.example.model.currency.ICurrencyInfo
import com.example.model.source.db.AppDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule constructor(private val context: Context)  {

    @Provides @Singleton
    fun provideCurrency(): ICurrencyInfo = CurrencyInfo()

    @Provides @Singleton
    fun provideCurrencyPresenter(): Contract.Presenter = CurrencyPresenter()

    @Provides @Singleton
    fun provideCurrencyActivity(): Contract.View = CurrencyActivity()

    @Provides @Singleton
    fun provideContext(): Context = context

    @Provides
    @Singleton
    fun providesDatabase(): AppDatabase {
        return AppDatabase.getInstance(context)!!
    }
}