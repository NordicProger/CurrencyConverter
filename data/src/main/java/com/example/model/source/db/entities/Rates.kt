package com.example.model.source.db.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "rates",
        indices = [
            Index("id")
        ],
        foreignKeys = [
            ForeignKey(
                entity = Currency::class,
                parentColumns = ["id"],
                childColumns = ["currencyId"],
                onDelete = ForeignKey.CASCADE
            )
        ])
class Rates (
    var currencyId: Int = 0,
    var currencyKey: String,
    var value: Float = 0f
    ){

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}