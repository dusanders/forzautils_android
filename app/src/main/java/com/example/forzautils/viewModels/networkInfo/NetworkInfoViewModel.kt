package com.example.forzautils.viewModels.networkInfo

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.forzautils.services.WiFiService
import com.example.forzautils.utils.Constants
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class NetworkInfoViewModel(wifiService: WiFiService) : ViewModel() {
  private val _tag = "NetworkInfoViewModel"

  data class InetInfo(
    val ip: String = Constants.Inet.DEFAULT_IP,
    val port: Int = Constants.Inet.PORT,
    val ssid: String = Constants.Inet.DEFAULT_SSID
  )

  private val _inetError: MutableStateFlow<Boolean> = MutableStateFlow(false)
  val inetError: StateFlow<Boolean> get() = _inetError

  private val _inetInfo: MutableStateFlow<InetInfo> = MutableStateFlow(InetInfo())
  val inetInfo: StateFlow<InetInfo> get() = _inetInfo

  init {
    Log.d(_tag, "Initializing")
    wifiService.inetState.observeForever {
      setInetState(it)
    }
  }

  private fun setInetState(inet: WiFiService.InetState) {
    viewModelScope.launch {
      Log.d(_tag, "setInetState: $inet ${_inetError.value}")
      if (inet.ipString == Constants.Inet.DEFAULT_IP) {
        _inetError.emit(true)
      } else {
        _inetError.emit(false)
        _inetInfo.emit(InetInfo(inet.ipString, inet.port, inet.ssid))
      }
    }
  }
}