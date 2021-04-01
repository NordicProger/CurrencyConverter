package com.example.currencyconverter.presenter

import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import com.example.currencyconverter.App
import com.example.currencyconverter.contract.Contract
import com.example.model.currency.ICurrencyCallBack
import com.example.model.currency.ICurrencyInfo
import com.example.model.entities.responses.CurrencyDTO
import com.example.model.source.db.AppDatabase
import com.example.model.source.db.entities.Rates
import kotlinx.coroutines.*
import java.lang.Error
import java.lang.Exception
import java.math.MathContext
import javax.inject.Inject


class CurrencyPresenter : Contract.Presenter {

    @Inject lateinit var currencyInfo: ICurrencyInfo
    @Inject lateinit var appDatabase: AppDatabase
    lateinit var currencyActivity: Contract.View
    lateinit var mContext: Context
    lateinit var pref: SharedPreferences
    lateinit var editor: SharedPreferences.Editor
    lateinit var fromCurrency: String
    lateinit var toCurrency: String
    lateinit var allRates: List<Rates>
    var sum: Float = 0f

    init {
        App.component.inject(this)
    }

    override fun onAttachView(view: Contract.View, context: Context) {
        currencyActivity = view
        mContext = context
        initPref()
    }

    private fun initPref() {
        pref = currencyActivity.getSharedPref()
        editor = pref.edit()
    }

    private fun loadCurrencyRate() {
        currencyInfo.getCurrencyInfo(object : ICurrencyCallBack{
            override fun onSuccess(currencyDTO: CurrencyDTO) {
                if(!currencyDTO.success){
                    currencyActivity.showAlert("Некоторые ошибки API")
                }
                try {
                    runBlocking {
                        withContext(Dispatchers.IO) {
                            //Chastoe obraschenie k baze s odnim elementom ne privetstvuetsa, no poskolku objektov malo to mozhno
                            // currencyId = 1 chtobi delat' replace zapicei v tablice
                            appDatabase.getCurrencyDao().insert(com.example.model.source.db.entities.Currency( 1, currencyDTO.base, currencyDTO.date, currencyDTO.timestamp ))
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
                    currencyActivity.showAlert(ex.message)
                }
            }

            override fun onError(error: String?) {
                currencyActivity.showAlert(error)
            }
        })

    }

    override fun checkSourceCurrency(sum: Float, fromCurrency: String, toCurrency: String) {
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
                currencyActivity.showAlert("Первый запуск и без интернета")
            }
        }
    }

    private fun recalcCurrency(from: Float, to: Float, sum: Float, source: EnumSource){
        val kursConvert = (to/from).toBigDecimal().round(MathContext(4)).toFloat()
        val recalcSum = (sum * kursConvert).toBigDecimal().round(MathContext(4)).toFloat()
        currencyActivity.onCurrencyChanged(recalcSum, kursConvert, source)
    }

    private fun isNetworkConnected(): Boolean {
        val cm = mContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        return cm!!.activeNetworkInfo != null && cm.activeNetworkInfo!!.isConnected
    }
}

enum class EnumSource(val value: String) {
    FromUrl("С сервера"),
    FromDB("Из базы");
}