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
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.net.Inet4Address
import java.net.NetworkInterface
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * Class to implement OS calls and callbacks with Android
 */
class WiFiService(
  private val _port: Int,
  private val _context: Context
) : NetworkCallback() {

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
    _context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

  private val _inetState: MutableLiveData<InetState?> = MutableLiveData(null)
  val inetState: LiveData<InetState?> get() = _inetState

  val port get() = _port;

  init {
    connectivityManager.registerDefaultNetworkCallback(this)
  }

  suspend fun waitForInet() {

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
    Log.d(_tag, "Capabilities changed")
    checkNetwork(network)
  }

  fun checkNetwork(network: Network) {
    Log.d(_tag, "Checking network...")
    // Offload the network check to a background thread
    CoroutineScope(Dispatchers.IO).launch {
      Log.d(_tag, "Checking network... inside offload thread")
      val networkCapabilities = connectivityManager
        .getNetworkCapabilities(network)
      if (networkCapabilities != null
        && networkCapabilities.hasTransport(
          NetworkCapabilities.TRANSPORT_WIFI
        )
      ) {
        Log.d(_tag, "Checking network...")
        val isAvailable = connectivityManager.activeNetwork
        if (isAvailable != null) {
          try {
            val ipString = (async { getIpAddress() }).await()
            val ssid = (async { requestSSID() }).await()
            if (_inetState.value?.ipString != ipString || _inetState.value?.ssid != ssid) {
              Log.d(_tag, "Update state: $ipString $ssid")
              _inetState.postValue(
                InetState(
                  ipString = ipString,
                  ssid = ssid,
                  port = _port
                )
              )
            }
          } catch (error: InterruptedException) {
            Log.w(_tag, "Interrupted while checking network...")
          }
        }
      } else {
        setInetUnavailable()
      }
    }
  }

  private fun setInetUnavailable() {
    val defaultState = InetState()
    Log.d(_tag, "Update state: ${defaultState.ipString} - ${defaultState.ssid}")
    _inetState.postValue(InetState())
  }

  private suspend fun requestSSID(): String = suspendCoroutine { continuation ->
    connectivityManager.registerNetworkCallback(
      NetworkRequest.Builder()
        .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
        .build(),
      @RequiresApi(Build.VERSION_CODES.S)
      object : NetworkCallback(FLAG_INCLUDE_LOCATION_INFO) {
        override fun onCapabilitiesChanged(
          network: Network,
          networkCapabilities: NetworkCapabilities
        ) {
          super.onCapabilitiesChanged(network, networkCapabilities)
          if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
            val wifiInfo = networkCapabilities.transportInfo as WifiInfo
            val ssidName = wifiInfo.ssid
            continuation.resume(ssidName.substring(1, ssidName.length - 1))
            connectivityManager.unregisterNetworkCallback(this)
          } else {
            continuation.resume(Constants.Inet.DEFAULT_SSID)
            connectivityManager.unregisterNetworkCallback(this)
          }
        }
      }
    )
  }

  private suspend fun getIpAddress(): String {
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
                ?: Constants.Inet.DEFAULT_IP
        }
      }
    }
    return ipStr
  }
}