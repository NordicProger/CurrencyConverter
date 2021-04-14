package com.example.currencyconverter.viewModel

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.currencyconverter.App
import com.example.currencyconverter.entities.CurrencyData
import com.example.model.currency.ICurrencyCallBack
import com.example.model.currency.ICurrencyInfo
import com.example.model.entities.responses.CurrencyDTO
import com.example.model.source.db.AppDatabase
import com.example.model.source.db.entities.Currency
import com.example.model.source.db.entities.Rates
import kotlinx.coroutines.*
import java.lang.Exception
import java.math.MathContext
import javax.inject.Inject

class CurrencyViewModel : ViewModel() {

    @Inject
    lateinit var currencyInfo: ICurrencyInfo
    @Inject
    lateinit var appDatabase: AppDatabase
    @SuppressLint("StaticFieldLeak")
    @Inject
    lateinit var context: Context
    lateinit var fromCurrency: String
    lateinit var toCurrency: String
    lateinit var allRates: List<Rates>
    var sum: Float = 0f
    private val APP_PREFERENCES: String = "MAIN"

    private var currencyData : MutableLiveData<CurrencyData> = MutableLiveData()
    private var errorMessage : MutableLiveData<String> = MutableLiveData()

    init {
        App.component.inject(this)
    }

    var pref = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)
    val editor: SharedPreferences.Editor = pref.edit()

    fun getDataCurrency() = currencyData
    fun getError() = errorMessage

    fun checkSourceCurrency(sum:Float, fromCurrency:String, toCurrency:String){
        this.fromCurrency = fromCurrency
        this.toCurrency = toCurrency
        this.sum = sum
        if (isNetworkConnected()){
            runBlocking {
                val job = CoroutineScope(Dispatchers.IO).async {
                    loadCurrencyRate()
                }
                job.await()
            }
        }else{
            if(pref.contains("BASE")){
                runBlocking {
                    val job = CoroutineScope(Dispatchers.IO).async {
                        allRates = appDatabase.getRatesDao().getAll
                    }
                    job.await()
                }
                recalcCurrency(allRates.first { it.currencyKey == fromCurrency }.value, allRates.first { it.currencyKey == toCurrency }.value, sum, EnumSource.FromDB)
            }else{
                errorMessage.value = "Первый запуск и без интернета"
            }
        }
    }

    private fun loadCurrencyRate() {
        currencyInfo.getCurrencyInfo(object : ICurrencyCallBack {
            override fun onSuccess(currencyDTO: CurrencyDTO) {
                if(!currencyDTO.success){
                    errorMessage.value = "Некоторые ошибки API"
                }
                try {
                    runBlocking {
                        withContext(Dispatchers.IO) {
                            //Chastoe obraschenie k baze s odnim elementom ne privetstvuetsa, no poskolku objektov malo to mozhno
                            // currencyId = 1 chtobi delat' replace zapicei v tablice
                            appDatabase.getCurrencyDao().insert(Currency( 1, currencyDTO.base, currencyDTO.date, currencyDTO.timestamp ))
                            appDatabase.getRatesDao().insert( Rates( 1,"RUB",currencyDTO.rates.rub) )
                            appDatabase.getRatesDao().insert( Rates( 1,"USD",currencyDTO.rates.usd) )
                            appDatabase.getRatesDao().insert( Rates( 1,"EUR",currencyDTO.rates.eur) )
                            appDatabase.getRatesDao().insert( Rates( 1,"GBP",currencyDTO.rates.gbp) )
                            appDatabase.getRatesDao().insert( Rates( 1,"CHF",currencyDTO.rates.chf) )
                            appDatabase.getRatesDao().insert( Rates( 1,"CNY",currencyDTO.rates.cny) )
                        }
                    }

                    editor.putString("BASE",currencyDTO.base)
                    editor.apply()

                    runBlocking {
                        val job = CoroutineScope(Dispatchers.IO).async {
                            allRates = appDatabase.getRatesDao().getAll
                        }
                        job.await()
                    }

                    recalcCurrency(allRates.first { it.currencyKey == fromCurrency }.value, allRates.first { it.currencyKey == toCurrency }.value, sum, EnumSource.FromUrl )
                }catch (ex: Exception){
                    errorMessage.value = ex.message
                }
            }

            override fun onError(error: String?) {
                errorMessage.value = error
            }
        })

    }

    private fun recalcCurrency(from: Float, to: Float, sum: Float, source: EnumSource){
        val kursConvert = (to/from).toBigDecimal().round(MathContext(4)).toFloat()
        val recalcSum = (sum * kursConvert).toBigDecimal().round(MathContext(4)).toFloat()
        currencyData.value = CurrencyData(recalcSum, kursConvert, source.value)

    }

    private fun isNetworkConnected(): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        return cm!!.activeNetworkInfo != null && cm.activeNetworkInfo!!.isConnected
    }
}

enum class EnumSource(val value: String) {
    FromUrl("С сервера"),
    FromDB("Из базы");
}