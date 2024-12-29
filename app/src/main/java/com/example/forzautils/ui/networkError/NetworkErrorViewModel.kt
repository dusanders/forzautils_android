package com.example.forzautils.ui.networkError

import androidx.lifecycle.ViewModel

class NetworkErrorViewModel : ViewModel() {

    interface Callback {
        fun onRetryNetworkClicked()
    }

    private lateinit var callback: Callback

    fun setCallback(callback: Callback) {
        this.callback = callback
    }

    fun onRetryClicked() {
        callback.onRetryNetworkClicked()
    }
}