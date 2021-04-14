package com.example.model.currency

import com.example.model.entities.responses.CurrencyDTO

interface ICurrencyCallBack {
    fun onSuccess(currencyDTO: CurrencyDTO)
    fun onError(error : String?)
}