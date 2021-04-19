package com.example.model.currency

import android.annotation.SuppressLint
import com.example.model.entities.responses.CurrencyDTO
import com.example.model.source.network.ICurrencyInfoService
import com.example.model.source.network.ServiceBuilder
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers.io


class CurrencyInfo : ICurrencyInfo {

    private lateinit var iCurrencyCallBack: ICurrencyCallBack

    @SuppressLint("CheckResult")
    override fun getCurrencyInfo(iCurrencyCallBack: ICurrencyCallBack) {
        this.iCurrencyCallBack = iCurrencyCallBack
        val apiService = ServiceBuilder.builder.create(ICurrencyInfoService::class.java)
        val currencyObservable = apiService.getCurrencyListInfo()
        currencyObservable.subscribeOn(io()).observeOn(AndroidSchedulers.mainThread()).subscribe(::handleResults, ::handleError)
    }

    private fun handleResults(currencyDTO: CurrencyDTO) {
        iCurrencyCallBack.onSuccess(currencyDTO)
    }

    private fun handleError(t: Throwable) {
        iCurrencyCallBack.onError(t.message)
    }
}