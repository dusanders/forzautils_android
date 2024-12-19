package com.example.forzautils.services

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import forza.telemetry.ForzaInterface
import forza.telemetry.ForzaTelemetryApi
import forza.telemetry.ForzaTelemetryBuilder
import forza.telemetry.VehicleData
import java.net.DatagramPacket

class ForzaService(private val port: Int) : ForzaInterface {

    private val _tag = "ForzaListener"

    private var telemetryBuilder: ForzaTelemetryBuilder? = null
    private var telemetryBuilderThread: Thread? = null

    private val _forzaListening: MutableLiveData<Boolean> = MutableLiveData(false)
    val forzaListening: LiveData<Boolean> get() = _forzaListening

    private val _connected: MutableLiveData<Boolean> = MutableLiveData(false)
    val forzaConnected: LiveData<Boolean> get() = _connected

    private val _data: MutableLiveData<ForzaTelemetryApi?> = MutableLiveData()
    val data: LiveData<ForzaTelemetryApi?> get() = _data

    fun start() {
        Log.w(_tag, "Starting Forza listener...")
        if(telemetryBuilder == null) {
            telemetryBuilder = ForzaTelemetryBuilder(port)
            telemetryBuilder?.addListener(this)
        }
        if(telemetryBuilderThread == null || telemetryBuilderThread!!.isInterrupted) {
            telemetryBuilderThread = telemetryBuilder?.thread
        }
        if(!telemetryBuilderThread?.isAlive!!) {
            telemetryBuilderThread?.start()
                ?: Log.w(_tag, "ForzaTelemetryBuilder Thread is null")
        }
        Log.d(_tag, "Posting Forza Listening")
        _forzaListening.postValue(true)
    }

    fun stop() {
        Log.w(_tag, "Stopping Forza listener...")
        telemetryBuilderThread?.interrupt()
        telemetryBuilder = null
        telemetryBuilderThread = null
    }

    override fun onDataReceived(api: ForzaTelemetryApi?) {
        _data.postValue(api)
    }

    override fun onConnected(api: ForzaTelemetryApi?, packet: DatagramPacket?) {
        _connected.postValue(true)
        if(api != null) {
            _data.postValue(api)
        }
    }

    override fun onGamePaused() {
        // Not used
    }

    override fun onGameUnpaused() {
        // Not used
    }

    override fun onCarChanged(api: ForzaTelemetryApi?, data: VehicleData?) {
        // Not used
    }
}