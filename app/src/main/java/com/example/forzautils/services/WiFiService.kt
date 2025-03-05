package com.example.forzautils.services

import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.wifi.WifiInfo
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.forzautils.utils.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.Inet4Address

/**
 * Class to implement OS calls and callbacks with Android
 */
class WiFiService(
  context: Context
) : NetworkCallback(FLAG_INCLUDE_LOCATION_INFO) {

  /**
   * State of the WiFi connection
   */
  data class InetState(
    var ipString: String,
    var ssid: String
  ) {
    override fun toString(): String {
      return "InetState(ipString='$ipString', ssid='$ssid')"
    }
  }

  private val _tag = "WiFiService"
  private var connectivityManager: ConnectivityManager =
    context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

  private val _inetState: MutableLiveData<InetState?> = MutableLiveData(null)
  val inetState: LiveData<InetState?> get() = _inetState

  init {
    connectivityManager.registerNetworkCallback(
      NetworkRequest.Builder()
        .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
        .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
        .build(),
      this
    )
  }

  fun stop() {
    try {
      connectivityManager.unregisterNetworkCallback(this)
    } catch (_: IllegalArgumentException) {
      // Safe to ignore - we may call this during android lifecycle events
    }
    _inetState.postValue(null)
  }

  override fun onUnavailable() {
    super.onUnavailable()
    Log.d(_tag, "Inet unavailable")
    setInetUnavailable()
  }

  override fun onLost(network: Network) {
    super.onLost(network)
    Log.d(_tag, "Inet lost")
    setInetUnavailable()
  }

  override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
    super.onCapabilitiesChanged(network, networkCapabilities)
    Log.d(_tag, "Capabilities changed: ${networkCapabilities.transportInfo}")
    checkNetwork(network, networkCapabilities)
  }

  private fun checkNetwork(network: Network, networkCapabilities: NetworkCapabilities) {
    // Offload the network check to a background thread
    CoroutineScope(Dispatchers.IO).launch {
      if (!networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
      ) {
        setInetUnavailable()
      } else {
        parseAndUpdateState(network, networkCapabilities)
      }
    }
  }

  private fun parseAndUpdateState(network: Network, networkCapabilities: NetworkCapabilities) {
    val ipString = getIPAddress(network)
    val ssid = getSSID(networkCapabilities)
    if (ipString == null || ssid == null) {
      setInetUnavailable()
      return
    }
    if (_inetState.value?.ipString != ipString || _inetState.value?.ssid != ssid) {
      _inetState.postValue(
        InetState(
          ipString = ipString,
          ssid = ssid
        )
      )
    }
  }

  private fun setInetUnavailable() {
    _inetState.postValue(null)
  }

  private fun getSSID(networkCapabilities: NetworkCapabilities): String? {
    var ssid: String? = null
    val wifiInfo = networkCapabilities.transportInfo as WifiInfo
    val ssidName = wifiInfo.ssid
    if (ssidName.isNotEmpty() && ssidName != Constants.Inet.ANDROID_UNKNOWN_SSID) {
      ssid = ssidName.substring(1, ssidName.length - 1)
    }
    return ssid
  }

  private fun getIPAddress(network: Network): String? {
    var result: String? = null
    val addresses = connectivityManager
      .getLinkProperties(network)
      ?.linkAddresses
    addresses?.forEach {
      if (it.address is Inet4Address && !it.address.isLoopbackAddress) {
        result = it.address.hostAddress
      }
    }
    return result
  }
}