package com.example.model.entities.responses

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class RatesDTO  {

    @SerializedName("RUB")
    var rub: Float = 0f

    @SerializedName("USD")
    var usd: Float = 0f

    @SerializedName("GBP")
    var gbp: Float = 0f

    @SerializedName("CNY")
    var cny: Float = 0f

    @SerializedName("CHF")
    var chf: Float = 0f

    @SerializedName("EUR")
    var eur: Float = 0f
}
