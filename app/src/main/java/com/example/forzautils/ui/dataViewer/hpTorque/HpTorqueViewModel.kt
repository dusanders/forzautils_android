package com.example.forzautils.ui.dataViewer.hpTorque

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.example.forzautils.services.ForzaService
import forza.telemetry.ForzaTelemetryApi

class HpTorqueViewModel : ViewModel() {
    interface Callback {
        fun onBackClicked()
    }

    data class HpTqData (
        var hp: Float = 0f,
        var tq: Float = 0f,
        var rpm: Float = 0f,
        var gear: Int = 0
    )

    private lateinit var forzaService: ForzaService
    private var callback: Callback? = null

    private val _hpTqData: MutableLiveData<HpTqData> = MutableLiveData()
    val hpTqData: LiveData<HpTqData> get() = _hpTqData

    private val dataObserver: Observer<ForzaTelemetryApi?> = Observer { data ->
        parseData(data)
    }

    override fun onCleared() {
        super.onCleared()
        removeObservers()
    }

    fun setCallback(callback: Callback){
        this.callback = callback
    }

    fun onBackClick() {
        callback?.onBackClicked()
    }

    fun setForzaService(forzaService: ForzaService) {
        this.forzaService = forzaService
        attachObservers()
    }

    private fun attachObservers() {
        forzaService.data.observeForever(dataObserver)
    }

    private fun removeObservers() {
        forzaService.data.removeObserver(dataObserver)
    }

    private fun parseData(data: ForzaTelemetryApi?) {
        val dataEntry = HpTqData()
        data?.horsePower.let { it ->
            it?.let { hp ->
                dataEntry.hp = hp
            }
        }
        data?.torque.let { it ->
            it?.let { torque ->
                dataEntry.tq = torque
            }
        }
        data?.currentEngineRpm.let { it ->
            it?.let { rpm ->
                dataEntry.rpm = rpm
            }
        }
        data?.gear.let { it ->
            it?.let { gear ->
                dataEntry.gear = gear
            }
        }
        _hpTqData.postValue(dataEntry)
    }
}