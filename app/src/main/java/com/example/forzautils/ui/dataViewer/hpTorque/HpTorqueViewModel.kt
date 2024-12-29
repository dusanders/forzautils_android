package com.example.forzautils.ui.dataViewer.hpTorque

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.example.forzautils.services.ForzaService
import forza.telemetry.ForzaTelemetryApi

// Alias these for easier code readability
typealias Gear = Int
typealias RPM = Int

class HpTorqueViewModel : ViewModel() {
    interface Callback {
        fun onBackClicked()
    }

    data class DataUpdate(
        var gear: Int,
        var rpm: Int
    )

    data class DataForRPM(
        var hp: Float = 0f,
        var tq: Float = 0f
    )

    private val _tag = "HpTorqueViewModel"
    private lateinit var forzaService: ForzaService
    private var callback: Callback? = null

    private val _dataUpdated: MutableLiveData<DataUpdate> = MutableLiveData()
    val dataUpdated: LiveData<DataUpdate> get() = _dataUpdated

    val dataMap: HashMap<Gear, HashMap<RPM, DataForRPM>> = HashMap()

    private val dataObserver: Observer<ForzaTelemetryApi?> = Observer { data ->
        parseData(data)
    }

    override fun onCleared() {
        super.onCleared()
        removeObservers()
    }

    fun clear() {
        dataMap.clear()
    }

    fun setCallback(callback: Callback) {
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

    private fun parseData(incoming: ForzaTelemetryApi?) {
        // Check for invalid data
        if (invalidData(incoming)) {
            return;
        }
        // We checked against null in the above check
        val data = incoming!!
        val roundedRpm = roundToNearestRpmRange(data.currentEngineRpm)
        if (!dataMap.containsKey(data.gear)) {
            dataMap[data.gear] = HashMap()
        }
        val gearMap = dataMap[data.gear]
        if (!gearMap!!.containsKey(roundedRpm)) {
            gearMap[roundedRpm] = DataForRPM(
                data.horsePower,
                data.torque
            )
        } else {
            val existingValues = gearMap[roundedRpm]
            if (existingValues!!.hp < data.horsePower) {
                gearMap[roundedRpm]!!.hp = data.horsePower
            }
            if (existingValues.tq < data.torque) {
                gearMap[roundedRpm]!!.tq = data.torque
            }
        }
        val eventData = DataUpdate(data.gear, roundedRpm)
        _dataUpdated.value = eventData
    }

    private fun invalidData(data: ForzaTelemetryApi?): Boolean {
        // We skip null data, gears less than first, and
        // Forza sends gear == 11 as a 'shift' event
        return (data == null
                || data.gear < 1
                || data.gear > 10
                // We skip data readings of negative values (usually decel)
                || data.horsePower <= 0
                || data.torque <= 0
                // Skip partial throttle events - only looking for full hp
                || data.throttle < 95
                // Skip power readings during a clutch event
                || data.clutch > 0)
    }

    private fun roundToNearestRpmRange(rpm: Float): Int {
        return Math.round(rpm / 100.0).toInt() * 100
    }
}