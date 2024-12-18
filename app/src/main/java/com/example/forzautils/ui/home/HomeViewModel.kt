package com.example.forzautils.ui.home

import android.util.Log
import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.forzautils.utils.OffloadThread
import java.net.Inet4Address
import java.net.NetworkInterface

class HomeViewModel : ViewModel() {
    companion object {
        val LOOPBACK_IP = "0.0.0.0"
    }

    enum class ForzaVersion {
        FM_2023,
        FM_7
    }

    data class InetState(
        var ipString: String = LOOPBACK_IP,
        var port: Int = 5300,
    )

    private val _tag = "HomeViewModel"
    private val _inetState: MutableLiveData<InetState> = MutableLiveData()
    val inetState: LiveData<InetState> get() = _inetState

    private val _version: MutableLiveData<ForzaVersion> = MutableLiveData()
    val version: LiveData<ForzaVersion> get() = _version

    fun setForzaVersion(version: ForzaVersion) {
        _version.postValue(version)
    }

    fun updateIpInfo() {
        OffloadThread.Instance()
            .post({
                var ipStr = ""
                val interfaces = NetworkInterface.getNetworkInterfaces()
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
                                        ?: LOOPBACK_IP
                        }
                    }
                }
                Log.d(_tag, "Got IP: $ipStr")
                setIp(ipStr)
            })
    }

    private fun setIp(ipStr: String) {
        val state = fromPrevious()
        state.ipString = ipStr
        _inetState.postValue(state)
    }

    private fun fromPrevious(): InetState {
        var state = _inetState.value
        if (state == null) {
            state = InetState()
        }
        return state;
    }
}