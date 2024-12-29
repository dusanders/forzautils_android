package com.example.forzautils.ui.dataViewer.dataOptions

import androidx.lifecycle.ViewModel

class DataOptionsViewModel : ViewModel() {
    interface Callback {
        fun onHpTorqueClick()
    }

    private lateinit var callback: Callback

    fun setCallback(callback: Callback) {
        this.callback = callback
    }

    fun userClick_hpTorque() {
        callback.onHpTorqueClick()
    }
}