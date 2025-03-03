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

class NetworkInfoViewModel(
  wifiService: WiFiService,
  forzaService: ForzaService
) : ViewModel() {
  private val _tag = "NetworkInfoViewModel"

  data class InetViewInfo(
    val ip: String = Constants.Inet.DEFAULT_IP,
    val port: Int = Constants.Inet.PORT,
    val ssid: String = Constants.Inet.DEFAULT_SSID
  ) {
    companion object {
      fun FromInetState(port: Int?, inet: WiFiService.InetState?): InetViewInfo {
        if (inet == null || port == null) {
          return InetViewInfo()
        }
        return InetViewInfo(inet.ipString, port, inet.ssid)
      }
    }
  }

  private var lastPort: Int? = null
  private var lastInet: WiFiService.InetState? = null

  private val _inetError: MutableStateFlow<Boolean> = MutableStateFlow(false)
  val inetError: StateFlow<Boolean> get() = _inetError

  private val _inetViewInfo: MutableStateFlow<InetViewInfo> = MutableStateFlow(
    InetViewInfo.FromInetState(
      forzaService.forzaListening.value,
      wifiService.inetState.value
    )
  )
  val inetViewInfo: StateFlow<InetViewInfo> get() = _inetViewInfo

  init {
    Log.d(_tag, "Initializing")
    wifiService.inetState.observeForever {
      lastInet = it
      setInetState()
    }
    forzaService.forzaListening.observeForever {
      lastPort = it
      setInetState()
    }
  }

  private fun setInetState() {
    viewModelScope.launch {
      Log.d(_tag, "setInetState: ${lastInet.toString()} ${_inetError.value}")
      if (lastInet != null && lastPort != null) {
        _inetViewInfo.emit(InetViewInfo.FromInetState(lastPort, lastInet))
      } else if (lastInet?.ipString == Constants.Inet.DEFAULT_IP) {
        _inetError.emit(true)
      } else {
        _inetError.emit(false)
      }
    }
  }
}