package com.example.model.source.db.dataAccessObjects

import androidx.room.*
import com.example.model.source.db.entities.Currency

@Dao
interface CurrencyDAO {

    @get:Query("SELECT * FROM currency")
    val getAll: List<Currency>

    @Query("select * from currency where id=:currencyId")
    fun getCurrency(currencyId: Int): Currency

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg currency: Currency)

    @Update
    fun update(vararg currency: Currency)

    @Delete
    fun delete(vararg currency: Currency)

    @Query("Delete from currency")
    fun deleteAll()

}