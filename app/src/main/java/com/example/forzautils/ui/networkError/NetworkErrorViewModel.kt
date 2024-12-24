package com.example.forzautils.ui.networkError

import androidx.lifecycle.ViewModel

class NetworkErrorViewModel(
    private val callback: Callback
) : ViewModel() {

    interface Callback {
        fun onRetryNetworkClicked()
    }

    fun onRetryClicked() {
        callback.onRetryNetworkClicked()
    }
}