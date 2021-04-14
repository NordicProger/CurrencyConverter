package com.example.model.entities.responses

import androidx.room.Entity
import androidx.room.Index
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Entity(tableName = "currency",
    indices = [Index(value = ["ratesId"], unique = true)])
class CurrencyDTO : Serializable {

    @SerializedName("rates")
    @Expose
    lateinit var rates: RatesDTO

    @SerializedName("base")
    @Expose
    lateinit var base: String

    @SerializedName("date")
    @Expose
    lateinit var date: String

    @SerializedName("success")
    @Expose
    var success: Boolean = false

    @SerializedName("timestamp")
    @Expose
    lateinit var timestamp: String

}