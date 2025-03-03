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
@RequiresApi(Build.VERSION_CODES.S)
class WiFiService(
  private val _port: Int,
  context: Context
) : NetworkCallback(FLAG_INCLUDE_LOCATION_INFO) {

  /**
   * State of the WiFi connection
   */
  data class InetState(
    var ipString: String = Constants.Inet.DEFAULT_IP,
    var port: Int = Constants.Inet.PORT,
    var ssid: String = Constants.Inet.DEFAULT_SSID
  ) {
    override fun toString(): String {
      return "InetState(ipString='$ipString', port=$port, ssid='$ssid')"
    }
  }

  private val _tag = "WiFiService"
  private var connectivityManager: ConnectivityManager =
    context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

  private val _inetState: MutableLiveData<InetState?> = MutableLiveData(null)
  val inetState: LiveData<InetState?> get() = _inetState

  val port get() = _port;

  init {
    connectivityManager.registerNetworkCallback(
      NetworkRequest.Builder()
        .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
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
    if (_inetState.value?.ipString != ipString || _inetState.value?.ssid != ssid) {
      _inetState.postValue(
        InetState(
          ipString = ipString,
          ssid = ssid,
          port = _port
        )
      )
    }
  }

  private fun setInetUnavailable() {
    _inetState.postValue(InetState())
  }

  private fun getSSID(networkCapabilities: NetworkCapabilities): String {
    var ssid = Constants.Inet.DEFAULT_SSID
    val wifiInfo = networkCapabilities.transportInfo as WifiInfo
    val ssidName = wifiInfo.ssid
    if (ssidName.isNotEmpty() && ssidName != Constants.Inet.ANDROID_UNKNOWN_SSID) {
      ssid = ssidName.substring(1, ssidName.length - 1)
    }
    return ssid
  }

  private fun getIPAddress(network: Network): String {
    var result = Constants.Inet.DEFAULT_IP
    val addresses = connectivityManager
      .getLinkProperties(network)
      ?.linkAddresses
    addresses?.forEach {
      if (it.address is Inet4Address && !it.address.isLoopbackAddress) {
        result = it.address.hostAddress ?: Constants.Inet.DEFAULT_IP
      }
    }
    return result
  }
}