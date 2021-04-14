package com.example.currencyconverter.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.ConnectivityManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.core.content.ContextCompat
import com.example.currencyconverter.R
import com.google.android.material.snackbar.Snackbar

enum class ConnectionType {
    WIFI,
    CELLULAR,
    NOINTERNET
}

class NetworkChangeReceiver constructor(activity: INetworkChange) : BroadcastReceiver() {

    private var isConnected = false
    private var context: Context? = null
    private var activity: INetworkChange = activity

    override fun onReceive(context: Context?, intent: Intent?) {
        val connectivityManager: ConnectivityManager = context!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        this.context = context
        if (isConnected) {
            @Suppress("DEPRECATION")
            if (connectivityManager.activeNetworkInfo != null && connectivityManager.activeNetworkInfo?.isConnected!!) {
                if (connectivityManager.activeNetworkInfo!!.type == ConnectivityManager.TYPE_WIFI) {
                    createSnackBar(ConnectionType.WIFI)
                }
                if (connectivityManager.activeNetworkInfo!!.type == ConnectivityManager.TYPE_MOBILE) {
                    createSnackBar(ConnectionType.CELLULAR)
                }
            } else {
                createSnackBar(ConnectionType.NOINTERNET)
            }
        }
        isConnected = true
    }


    private fun createSnackBar(type: ConnectionType){
        val snack: Snackbar = activity.provideSnackBar()
        if(type == ConnectionType.WIFI){
            snack.setText("WI-FI connection")
            snack.setBackgroundTint(ContextCompat.getColor(context!!, R.color.success_notification))
        }
        if(type == ConnectionType.CELLULAR){
            snack.setText("Using Mobile Data")
            snack.setBackgroundTint(ContextCompat.getColor(context!!, R.color.warning_notification))
        }
        if(type == ConnectionType.NOINTERNET){
            snack.setText("Connection Lost")
            snack.setBackgroundTint(ContextCompat.getColor(context!!, R.color.danger_notification))
        }
        snack.setTextColor(Color.WHITE)
        snack.setAction("Skip") {
            snack.dismiss()
        }.setActionTextColor(Color.WHITE)
        snack.show()
        val v: Vibrator = context!!.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            v.vibrate(100)
        }
    }

}