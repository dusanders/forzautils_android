package com.example.forzautils.ui.dataViewer.hpTorque

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.example.forzautils.services.ForzaService
import forza.telemetry.ForzaTelemetryApi
import kotlin.math.round

typealias Gear = Int
typealias RPM = Int

class HpTorqueViewModel : ViewModel() {
    interface Callback {
        fun onBackClicked()
    }

    private data class HpTqData(
        var hp: Float = 0f,
        val tq: Float = 0f,
        val rpm: Float = 0f,
        val gear: Int = 0
    )

    data class DataUpdate(
        var gear: Int,
        var rpm: Int
    )

    data class DataForRPM(
        var hp: Float = 0f,
        var tq: Float = 0f
    )

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

    fun ADD_DEBUG() {
        handleParsedData(HpTqData(100f, 423f, 1278f, 1))
        handleParsedData(HpTqData(123f, 440f, 1340f, 1))
        handleParsedData(HpTqData(134f, 450f, 1598f, 1))
        handleParsedData(HpTqData(110f, 309f, 1832f, 1))
        handleParsedData(HpTqData(90f, 254f, 2231f, 1))
        handleParsedData(HpTqData(300f, 210f, 1300f, 2))
        handleParsedData(HpTqData(310f, 220f, 1400f, 2))
        handleParsedData(HpTqData(320f, 320f, 1500f, 2))
        handleParsedData(HpTqData(320f, 350f, 1600f, 2))
        handleParsedData(HpTqData(300f, 210f, 1300f, 3))
        handleParsedData(HpTqData(310f, 220f, 1400f, 3))
        handleParsedData(HpTqData(320f, 320f, 1500f, 3))
        handleParsedData(HpTqData(320f, 350f, 1600f, 3))
        handleParsedData(HpTqData(300f, 210f, 1300f, 4))
        handleParsedData(HpTqData(310f, 220f, 1400f, 4))
        handleParsedData(HpTqData(320f, 320f, 1500f, 4))
        handleParsedData(HpTqData(320f, 350f, 1600f, 4))
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

    private fun handleParsedData(data: HpTqData) {
        val roundedRpm = roundToNearestRpmRange(data.rpm)
        if (!dataMap.containsKey(data.gear)) {
            dataMap[data.gear] = HashMap()
        }
        val gearMap = dataMap[data.gear]
        if (!gearMap!!.containsKey(roundedRpm)) {
            gearMap[roundedRpm] = DataForRPM(
                data.hp,
                data.tq
            )
        } else {
            val existingValues = gearMap[roundedRpm]
            if (existingValues!!.hp != data.hp) {
                gearMap[roundedRpm]!!.hp = data.hp
            }
            if (existingValues.tq != data.tq) {
                gearMap[roundedRpm]!!.tq = data.tq
            }
        }
        val eventData = DataUpdate(data.gear, roundedRpm)
        _dataUpdated.value = eventData
//        _dataUpdated.postValue(eventData)
    }

    private fun parseData(data: ForzaTelemetryApi?) {
        if (data == null || data.gear < 1 || data.gear > 10) {
            return;
        }
        val dataPacket = HpTqData(
            data.horsePower,
            data.torque,
            data.currentEngineRpm,
            data.gear
        )
        handleParsedData(dataPacket)
    }

    private fun roundToNearestRpmRange(rpm: Float): Int {
        return Math.round(rpm / 100.0).toInt() * 100
    }
}