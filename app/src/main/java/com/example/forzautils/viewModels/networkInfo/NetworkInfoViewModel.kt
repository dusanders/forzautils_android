package com.example.forzautils.viewModels.networkInfo

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.forzautils.services.ForzaService
import com.example.forzautils.services.WiFiService
import com.example.forzautils.utils.Constants
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

enum class ConnectionStates {
  CONNECTING,
  NO_WIFI,
  FORZA_OPEN,
}

class NetworkInfoViewModel(
  wifiService: WiFiService,
  forzaService: ForzaService
) : ViewModel() {
  private val _tag = "NetworkInfoViewModel"

  data class InetViewInfo(
    val ip: String,
    val port: Int,
    val ssid: String
  )

  private val _connectionState: MutableStateFlow<ConnectionStates> =
    MutableStateFlow(ConnectionStates.NO_WIFI)
  val connectionState: StateFlow<ConnectionStates> get() = _connectionState

  private var lastInet: WiFiService.InetState? = null
  private var lastPort: Int? = null

  init {
    Log.d(_tag, "Initializing")
    wifiService.inetState.observeForever {
      lastInet = it
      handleNetUpdate()
    }
    forzaService.forzaListening.observeForever {
      lastPort = it
      handleNetUpdate()
    }
  }

  fun getInetInfo(): InetViewInfo {
    return InetViewInfo(
      lastInet!!.ipString,
      lastPort!!,
      lastInet!!.ssid
    )
  }

  fun handleNetUpdate() {
    viewModelScope.launch {
      if(lastInet == null){
        _connectionState.emit(ConnectionStates.NO_WIFI)
      } else if(lastPort == null){
        _connectionState.emit(ConnectionStates.CONNECTING)
      } else {
        _connectionState.emit(ConnectionStates.FORZA_OPEN)
      }
    }
  }
}