package com.example.forzautils.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.forzautils.services.WiFiService
import com.example.forzautils.utils.Constants

class NetworkInfoViewModel(private val wifiService: WiFiService) : ViewModel() {
    private val _tag = "NetworkInfoViewModel"

    data class InetInfo(
        val ip: String,
        val port: Int,
        val ssid: String
    )

    private val _inetError: MutableLiveData<Boolean> = MutableLiveData()
    val inetError: LiveData<Boolean> get() = _inetError

    private val _inetInfo: MutableLiveData<InetInfo> = MutableLiveData()
    val inetInfo: LiveData<InetInfo> get() = _inetInfo

    fun setInetState(inet: WiFiService.InetState) {
        if (inet.ipString == Constants.Inet.DEFAULT_IP) {
            _inetError.postValue(true)
        } else {
            _inetError.postValue(false)
            _inetInfo.postValue(InetInfo(inet.ipString, inet.port, inet.ssid))
        }
    }
}