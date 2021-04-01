package com.example.currencyconverter.contract

import android.content.Context
import android.content.SharedPreferences
import com.example.currencyconverter.presenter.EnumSource

interface Contract {

    interface View {
        fun onCurrencyChanged(sum: Float, kursConvert: Float, source: EnumSource)
        fun getSharedPref() : SharedPreferences
        fun showAlert(message: String?)
    }

    interface Presenter {
        fun checkSourceCurrency(sum:Float, fromCurrency:String, toCurrency:String)
        fun onAttachView(view : View, context: Context)
    }

}