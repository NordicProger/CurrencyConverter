package com.example.model.source.network

import com.example.model.entities.responses.CurrencyDTO
import io.reactivex.Observable
import io.reactivex.Single
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.GET

interface ICurrencyInfoService {

    @GET("/v1/latest?access_key=523eba64351233008da487fe88459426&base=EUR&symbols=USD,GBP,RUB,CNY,EUR,CHF")
    fun getCurrencyListInfo(): Single<CurrencyDTO>

}