package com.example.forzautils.ui.networkInfo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.forzautils.services.WiFiService
import com.example.forzautils.utils.Constants

class NetworkInfoViewModel : ViewModel() {
    private val _tag = "NetworkInfoViewModel"

    data class InetInfo(
        val ip: String,
        val port: Int
    )

    private val _inetError: MutableLiveData<Boolean> = MutableLiveData()
    val inetError: LiveData<Boolean> get() = _inetError

    private val _inetInfo: MutableLiveData<InetInfo> = MutableLiveData()
    val inetInfo: LiveData<InetInfo> get() = _inetInfo

    private val _readyBtnClicked: MutableLiveData<Boolean> = MutableLiveData()
    val readyBtnClicked: LiveData<Boolean> get() = _readyBtnClicked

    fun onReadyClicked() {
        _readyBtnClicked.postValue(true);
    }

    fun setInetState(inet: WiFiService.InetState) {
        if (inet.ipString == Constants.DEFAULT_IP) {
            _inetError.postValue(true)
        } else {
            _inetError.postValue(false)
            _inetInfo.postValue(InetInfo(inet.ipString, inet.port))
        }
    }
}