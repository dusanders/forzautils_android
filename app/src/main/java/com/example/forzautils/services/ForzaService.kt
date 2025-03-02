package com.example.forzautils.services

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.forzautils.utils.Constants
import forza.telemetry.data.ForzaInterface
import forza.telemetry.data.ForzaTelemetryBuilder
import forza.telemetry.data.TelemetryData
import forza.telemetry.data.database.DatabaseService
import java.net.DatagramPacket

/**
 * Class to implement logic and callbacks for the Forza Telemetry module
 */
class ForzaService(
  private val wifiService: WiFiService,
  val context: Context
) : ForzaInterface {
  // Debug tag
  private val _tag = "ForzaService"

  // Reference to the telemetry builder
  private var telemetryBuilder: ForzaTelemetryBuilder? = null

  // Background thread reference
  private var telemetryBuilderThread: Thread? = null

  // Port from wifi service
  private var port: Int = -1

  // UDP Listening state
  private val _forzaListening: MutableLiveData<Boolean> = MutableLiveData(false)
  val forzaListening: LiveData<Boolean> get() = _forzaListening

  // Data state
  private val _data: MutableLiveData<TelemetryData?> = MutableLiveData()
  val data: LiveData<TelemetryData?> get() = _data

  private val wifiInetObserver: Observer<WiFiService.InetState?> = Observer { inet ->
    Log.d(_tag, "Inet state changed to ${inet?.ipString} ${inet?.ssid}")
    if (inet?.ipString != Constants.Inet.DEFAULT_IP) {
      startForzaUdpListen()
    } else {
      stop()
    }
  }

  init {
    Handler(Looper.getMainLooper()).post {
      this.port = wifiService.port
      wifiService.inetState.observeForever(wifiInetObserver)
    }
  }

  /**
   * Stops the Forza listener
   */
  fun stop() {
    Log.w(_tag, "Stopping Forza listener...")
    telemetryBuilderThread?.interrupt()
    telemetryBuilder = null
    telemetryBuilderThread = null
    _forzaListening.postValue(false)
  }

  override fun onDataReceived(api: TelemetryData?) {
    _data.postValue(api)
  }

  override fun onConnected(api: TelemetryData?, packet: DatagramPacket?) {
    _forzaListening.postValue(true)
    if (api != null) {
      _data.postValue(api)
    }
  }

  override fun onGamePaused() {
    // Not used
  }

  override fun onGameUnpaused() {
    // Not used
  }

  private fun startForzaUdpListen() {
    Log.w(_tag, "Starting Forza listener...")
    if (telemetryBuilder == null) {
      telemetryBuilder = ForzaTelemetryBuilder(port, context)
      telemetryBuilder?.addListener(this)
    }
    if (telemetryBuilderThread == null || telemetryBuilderThread!!.isInterrupted) {
      telemetryBuilderThread = telemetryBuilder?.thread
    }
    if (!telemetryBuilderThread?.isAlive!!) {
      telemetryBuilderThread?.start()
        ?: Log.w(_tag, "ForzaTelemetryBuilder Thread is null")
    }
    _forzaListening.postValue(true)
  }
}