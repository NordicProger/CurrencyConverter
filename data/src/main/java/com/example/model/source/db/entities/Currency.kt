package com.example.model.source.db.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Entity(tableName = "currency"
        //,indices = [Index(value = ["ratesId"], unique = true)]
)
class Currency(
    @PrimaryKey var id: Int = 0,
    var base: String,
    var date: String,
    var timestamp: String
)