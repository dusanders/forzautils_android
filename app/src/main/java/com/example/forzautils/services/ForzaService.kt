package com.example.forzautils.services

import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.forzautils.utils.Constants
import forza.telemetry.data.ForzaUdpSocket
import forza.telemetry.data.TelemetryData
import forza.telemetry.data.types.ForzaUdpSocketEvents
import java.net.SocketException

interface ForzaServiceCallbacks {
  fun onSocketException(e: SocketException)
}

/**
 * Class to implement logic and callbacks for the Forza Telemetry module
 */
class ForzaService(
  private val wifiService: WiFiService,
  val context: Context,
  private val callbacks: ForzaServiceCallbacks? = null
) {
  // Debug tag
  private val _tag = "ForzaService"

  private var port = Constants.Inet.PORT

  private var forzaUdpSocket: ForzaUdpSocket

  // UDP Listening state
  private val _forzaListening: MutableLiveData<Int?> = MutableLiveData(null)
  val forzaListening: LiveData<Int?> get() = _forzaListening

  // Data state
  private val _data: MutableLiveData<TelemetryData?> = MutableLiveData()
  val data: LiveData<TelemetryData?> get() = _data

  private val _wifiInet: MutableLiveData<WiFiService.InetState?> = MutableLiveData()
  val wifiInet: LiveData<WiFiService.InetState?> get() = _wifiInet

  private val forzaSocketEvent = object : ForzaUdpSocketEvents {
    override fun onSocketError(e: Exception) {
      callbacks?.onSocketException(e as SocketException)
      _forzaListening.postValue(null)
    }

    override fun onData(data: TelemetryData) {
      _data.postValue(data)
    }

    override fun onOpen(port: Int) {
      _forzaListening.postValue(port)
    }
  }

  init {
    forzaUdpSocket = ForzaUdpSocket(context, forzaSocketEvent)
    Handler(Looper.getMainLooper()).post {
      wifiService.inetState.observeForever { postNewInet(it) }
    }
  }

  fun start() {
    forzaUdpSocket.bind(port)
  }

  /**
   * Stops the Forza listener
   */
  fun stop() {
    Log.w(_tag, "Stopping Forza listener...")
    forzaUdpSocket.stop()
    _forzaListening.postValue(null)
  }

  private fun postNewInet(inet: WiFiService.InetState?) {
    _wifiInet.postValue(inet)
    if(inet != null && inet.ipString != Constants.Inet.DEFAULT_IP) {
      if(!forzaUdpSocket.isBound) {
        start()
      }
    }
  }

}