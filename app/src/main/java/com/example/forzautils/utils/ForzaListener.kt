package com.example.forzautils.utils

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import forza.telemetry.ForzaInterface
import forza.telemetry.ForzaTelemetryApi
import forza.telemetry.ForzaTelemetryBuilder
import forza.telemetry.VehicleData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.net.DatagramPacket
import java.net.Inet4Address
import java.net.NetworkInterface

class ForzaListener(port: Int) : ForzaInterface {
    data class InetState (
        var ipString: String = Constants.DEFAULT_IP,
        var port: Int = Constants.PORT
    )

    private val _tag = "ForzaListener"
    private val _port: Int = port
    private var telemetryBuilder: ForzaTelemetryBuilder? = null
    private var telemetryBuilderThread: Thread? = null

    private val _inetState: MutableLiveData<InetState> = MutableLiveData()
    val inetState: LiveData<InetState> get() = _inetState

    private val _forzaListening: MutableLiveData<Boolean> = MutableLiveData(false)
    val forzaListening: LiveData<Boolean> get() = _forzaListening

    private val _connected: MutableLiveData<Boolean> = MutableLiveData(false)
    val forzaConnected: LiveData<Boolean> get() = _connected

    private val _data: MutableLiveData<ForzaTelemetryApi?> = MutableLiveData()
    val data: LiveData<ForzaTelemetryApi?> get() = _data

    private suspend fun updateInet(): InetState {
        var ipStr = ""
        val interfaces = withContext(Dispatchers.IO) {
            NetworkInterface.getNetworkInterfaces()
        }
        while (interfaces.hasMoreElements()) {
            val inetDevice = interfaces.nextElement()
            if (inetDevice.isLoopback) {
                continue
            }
            val addresses = inetDevice.inetAddresses
            while (addresses.hasMoreElements()) {
                val address = addresses.nextElement()
                if (address.isLoopbackAddress) {
                    continue
                }
                if (address is Inet4Address) {
                    ipStr = address.hostAddress
                        ?: address.hostAddress
                                ?: Constants.DEFAULT_IP
                }
            }
        }
        Log.d(_tag, "Got IP: $ipStr")
        return InetState(ipStr, _port)
    }

    private suspend fun offloadStart(): Unit = coroutineScope {
        val inet = async { updateInet() }
        val inetInfo = inet.await()
        if(!telemetryBuilderThread?.isAlive!!) {
            Log.d(_tag, "Starting builder thread...")
            telemetryBuilderThread?.start()
                ?: Log.w(_tag, "ForzaTelemetryBuilder Thread is null")
        }
        Log.d(_tag, "Posting inet: ${inetInfo.ipString}")
        _inetState.postValue(inetInfo)
        _forzaListening.postValue(true)
    }

    fun start() {
        Log.w(_tag, "Starting Forza listener...")
        if(telemetryBuilder == null) {
            telemetryBuilder = ForzaTelemetryBuilder(_port)
            telemetryBuilder?.addListener(this)
        }
        if(telemetryBuilderThread == null || telemetryBuilderThread!!.isInterrupted) {
            telemetryBuilderThread = telemetryBuilder?.thread
        }
        OffloadThread.Instance().post {
            runBlocking {
                offloadStart()
            }
        }
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