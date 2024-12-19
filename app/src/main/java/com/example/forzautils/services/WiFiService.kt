package com.example.forzautils.services

import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.forzautils.utils.Constants
import com.example.forzautils.utils.OffloadThread
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.net.Inet4Address
import java.net.NetworkInterface

class WiFiService(
    private val _port: Int,
    private val _context: Context
) : NetworkCallback() {
    data class InetState (
        var ipString: String = Constants.DEFAULT_IP,
        var port: Int = Constants.PORT
    )

    private val _tag = "WiFiService"
    private var connectivityManager: ConnectivityManager = _context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private val _inetState: MutableLiveData<InetState> = MutableLiveData()
    val inetState: LiveData<InetState> get() = _inetState

    val port get() = _port;

    init {
        connectivityManager.registerDefaultNetworkCallback(this)
    }

    fun stop() {
        connectivityManager.unregisterNetworkCallback(this)
    }

    override fun onUnavailable() {
        super.onUnavailable()
        setInetUnavailable()
    }

    override fun onLost(network: Network) {
        super.onLost(network)
        setInetUnavailable()
    }

    override fun onAvailable(network: Network) {
        super.onAvailable(network)
        checkNetwork()
    }

    fun checkNetwork() {
        OffloadThread.Instance().post({
            val isAvailable = connectivityManager.activeNetwork
            if(isAvailable != null) {
                runBlocking {
                    val inetState = (async { updateInet() }).await()
                    _inetState.postValue(inetState)
                }
            }
        })
    }

    private fun setInetUnavailable() {
        _inetState.postValue(InetState(Constants.DEFAULT_IP, _port))
    }

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
}