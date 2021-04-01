package com.example.currencyconverter.util

import com.google.android.material.snackbar.Snackbar

interface INetworkChange {
    fun provideSnackBar() : Snackbar
}