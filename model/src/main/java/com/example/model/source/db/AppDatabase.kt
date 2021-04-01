package com.example.model.source.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.model.source.db.dataAccessObjects.CurrencyDAO
import com.example.model.source.db.dataAccessObjects.RatesDAO
import com.example.model.source.db.entities.Currency
import com.example.model.source.db.entities.Rates

@Database(entities = [
    Currency::class,
    Rates::class
],
    version = 5)
abstract class AppDatabase : RoomDatabase(){
    companion object {

        private const val DB_NAME = "AppDatabase.db"

        @Volatile
        private var instance: AppDatabase? = null

        @Synchronized
        fun getInstance(context: Context): AppDatabase? {
            if (instance == null) {
                instance = create(context)
            }
            return instance
        }

        private fun create(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                DB_NAME
            )
                .fallbackToDestructiveMigration()
                .build()
        }
    }
    abstract fun getCurrencyDao(): CurrencyDAO
    abstract fun getRatesDao(): RatesDAO
}