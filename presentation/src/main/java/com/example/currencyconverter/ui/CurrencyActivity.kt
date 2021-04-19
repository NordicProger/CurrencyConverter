package com.example.currencyconverter.ui

import android.content.IntentFilter
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.currencyconverter.R
import kotlinx.android.synthetic.main.activity_currency.*
import com.example.currencyconverter.viewModel.CurrencyViewModel
import com.example.currencyconverter.util.INetworkChange
import com.example.currencyconverter.util.NetworkChangeReceiver
import com.google.android.material.snackbar.Snackbar

class CurrencyActivity : AppCompatActivity(), INetworkChange {

    private val currencyList = arrayListOf("RUB", "USD", "EUR", "GBP", "CHF", "CNY")
    lateinit var pref : SharedPreferences
    private val APP_PREFERENCES: String = "MAIN"
    private val CURRENCYFROM: String = "CURRENCYFROM"
    private val CURRENCYTO: String = "CURRENCYTO"
    private val SUM: String = "SUM"
    var firstOpenFromFlag = false
    var firstOpenToFlag = false
    private lateinit var eventReceiver: NetworkChangeReceiver
    private lateinit var intentFilter: IntentFilter

    private val currencyViewModel by lazy { ViewModelProvider(this)[CurrencyViewModel::class.java] }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_currency)

        initView()
        pref = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE)

        // dropDown Currency From
        val adapterFrom = ArrayAdapter( this, android.R.layout.simple_list_item_1, currencyList)
        adapterFrom.setDropDownViewResource(android.R.layout.select_dialog_singlechoice)

        currencyFrom.setPositiveButton(resources?.getString(R.string.close))
        currencyFrom.setTitle(resources?.getString(R.string.title))
        currencyFrom.adapter = adapterFrom
        currencyFrom.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (firstOpenFromFlag) {
                    recalcCurrency()
                }
                firstOpenFromFlag = true
            }
        }

        //RxAdapterView.itemSelections(currencyFrom).observeOn(AndroidSchedulers.mainThread()).compose {  }

        // dropDown Currency To
        val adapterTo = ArrayAdapter( this, android.R.layout.simple_list_item_1, currencyList)
        adapterTo.setDropDownViewResource(android.R.layout.select_dialog_singlechoice)

        currencyTo.setPositiveButton(resources?.getString(R.string.close))
        currencyTo.setTitle(resources?.getString(R.string.title))
        currencyTo.adapter = adapterTo
        currencyTo.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (firstOpenToFlag) {
                    recalcCurrency()
                }
                firstOpenToFlag = true
            }
        }

        if (pref.contains(CURRENCYFROM)) {
            currencyFrom.setSelection(currencyList.indexOf(pref.getString(CURRENCYFROM,"")))
        }
        if (pref.contains(CURRENCYTO)) {
            currencyTo.setSelection(currencyList.indexOf(pref.getString(CURRENCYTO,"")))
        }
        if (pref.contains(SUM)) {
            currSum.setText(pref.getFloat(SUM,0f).toString())
        }

        /*currSum.doOnTextChanged { charSequence: CharSequence?, i: Int, i1: Int, i2: Int ->
            recalcCurrency()
        }*/

        //RxTextView.textChanges(currSum).debounce(1, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread()).compose(recalcCurrency())

        recalcCurrency()

        intentFilter = IntentFilter()
        intentFilter.addAction(getString(R.string.CONNECTIVITY_CHANGE))
        eventReceiver = NetworkChangeReceiver(this)

        currencyViewModel.getDataCurrency().observe(this, Observer { list ->
            recalcSum.text = list.summ.toString()
            currConvert.text = list.curs.toString()
            source.text = list.source
            stopLoader()
        })

        currencyViewModel.getError().observe(this, Observer {
            showAlert(it)
        })

        /*val checkSourceCurrency = ObservableTransformer<String, String>{ observable ->

        }*/
    }

    private fun startLoader(){
        progressCircular.visibility = View.VISIBLE
    }

    private fun stopLoader(){
        progressCircular.visibility = View.GONE
    }

    private fun initView() {
        currSum.doOnTextChanged { text, start, before, count ->
            recalcCurrency()
        }
    }

    private fun recalcCurrency(){
        if(currSum.text.isEmpty() || currSum.text.last() == '.' || (currSum.text.toString().toFloat()) < 0f){
            return
        }
        startLoader()
        currencyViewModel.checkSourceCurrency(currSum.text.toString().toFloat(), currencyFrom.selectedItem.toString(), currencyTo.selectedItem.toString())
    }

    override fun provideSnackBar(): Snackbar = Snackbar.make(findViewById(R.id.mainPageLayout), "", Snackbar.LENGTH_LONG)

    private fun showAlert(message: String?) {

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Ошибка")
        builder.setMessage(message)
        builder.setNeutralButton("Ok"){_,_ -> }
        val dialog: AlertDialog = builder.create()
        dialog.show()
        progressCircular.visibility = View.GONE
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(eventReceiver, intentFilter)
    }

    override fun onStop() {
        super.onStop()
        val editor: SharedPreferences.Editor = pref.edit()
        editor.putString(CURRENCYFROM, currencyFrom.selectedItem.toString())
        editor.putString(CURRENCYTO, currencyTo.selectedItem.toString())
        editor.putFloat(SUM, currSum.text.toString().toFloat())
        editor.apply()
        unregisterReceiver(eventReceiver)
    }
}