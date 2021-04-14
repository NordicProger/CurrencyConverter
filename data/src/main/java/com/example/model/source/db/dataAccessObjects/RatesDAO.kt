package com.example.model.source.db.dataAccessObjects

import androidx.room.*
import com.example.model.source.db.entities.Rates

@Dao
interface RatesDAO {

    @get:Query("SELECT * FROM rates")
    val getAll: List<Rates>

    @Query("select * from rates where id=:ratesId")
    fun getRates(ratesId: Int): Rates

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg rates: Rates)

    @Update
    fun update(vararg rates: Rates)

    @Delete
    fun delete(vararg rates: Rates)

    @Query("Delete from rates")
    fun deleteAll()
}