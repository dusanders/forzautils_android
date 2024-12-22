package com.example.forzautils.ui.dataViewer.dataOptions

import androidx.lifecycle.ViewModel
import com.example.forzautils.services.WiFiService

class DataOptionsViewModel(private val callback: Callback) : ViewModel() {
    interface Callback {
        fun onHpTorqueClick()
    }

    fun userClick_hpTorque() {
        callback.onHpTorqueClick()
    }
}