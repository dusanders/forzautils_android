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
import forza.telemetry.data.ForzaConstants
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

  private val _rawBytes: MutableLiveData<ByteArray> = MutableLiveData()
  val rawBytes: LiveData<ByteArray> get() = _rawBytes

  // Data state
  private val _data: MutableLiveData<TelemetryData?> = MutableLiveData()
  val data: LiveData<TelemetryData?> get() = _data

  private val forzaSocketEvent = object : ForzaUdpSocketEvents {
    override fun onSocketError(e: Exception) {
      callbacks?.onSocketException(e as SocketException)
      _forzaListening.postValue(null)
    }

    override fun onData(data: TelemetryData) {
//      Log.d(_tag, "data: isFM8: ${data.gameVersion == ForzaConstants.GameVersion.MOTORSPORT_8} " +
//          "${data.currentEngineRpm} ${data.timeStampMS} ${data.isRaceOn}"
//      )
      _data.postValue(data)
      _rawBytes.postValue(data.rawBytes)
    }

    override fun onOpen(port: Int) {
      _forzaListening.postValue(port)
    }
  }

  init {
    forzaUdpSocket = ForzaUdpSocket(context, forzaSocketEvent)
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
}