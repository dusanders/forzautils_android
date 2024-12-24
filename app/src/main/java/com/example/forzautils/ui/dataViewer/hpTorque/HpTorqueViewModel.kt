package com.example.forzautils.ui.dataViewer.hpTorque

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.example.forzautils.services.ForzaService
import forza.telemetry.ForzaTelemetryApi

class HpTorqueViewModel(private val forzaService: ForzaService) : ViewModel() {

    private val _hp: MutableLiveData<Float> = MutableLiveData(0f)
    val hp: LiveData<Float> get() = _hp;

    private val _torque: MutableLiveData<Float> = MutableLiveData(0f)
    val torque: LiveData<Float> get() = _torque

    private val _rpm: MutableLiveData<Float> = MutableLiveData(0f)
    val rpm: LiveData<Float> get() = _rpm

    private val _gear: MutableLiveData<Int> = MutableLiveData(0)
    val gear: LiveData<Int> get() = _gear

    private val dataObserver: Observer<ForzaTelemetryApi?> = Observer { data ->
        parseData(data)
    }

    init {
        attachObservers()
    }

    override fun onCleared() {
        super.onCleared()
        removeObservers()
    }

    private fun attachObservers() {
        forzaService.data.observeForever(dataObserver)
    }

    private fun removeObservers() {
        forzaService.data.removeObserver(dataObserver)
    }

    private fun parseData(data: ForzaTelemetryApi?) {
        data?.horsePower.let { it ->
            it?.let { hp ->
                _hp.postValue(hp)
            }
        }
        data?.torque.let { it ->
            it?.let { torque ->
                _torque.postValue(torque)
            }
        }
        data?.currentEngineRpm.let { it ->
            it?.let { rpm ->
                _rpm.postValue(rpm)
            }
        }
    }
}