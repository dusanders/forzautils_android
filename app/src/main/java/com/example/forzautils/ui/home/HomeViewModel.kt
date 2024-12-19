package com.example.forzautils.ui.home

import android.util.Log
import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.forzautils.utils.Constants
import com.example.forzautils.utils.ForzaListener
import com.example.forzautils.utils.OffloadThread
import java.net.Inet4Address
import java.net.NetworkInterface

class HomeViewModel: ViewModel() {
    private val _tag = "HomeViewModel"
    data class InetInfo (
        val ip: String,
        val port: Int
    )
    init {
        Log.d(_tag, "init")
    }
    private val _inetError: MutableLiveData<Boolean> = MutableLiveData()
    val inetError: LiveData<Boolean> get() = _inetError

    private val _inetInfo: MutableLiveData<InetInfo> = MutableLiveData()
    val inetInfo: LiveData<InetInfo> get() = _inetInfo

    private val _version: MutableLiveData<Constants.ForzaVersion> = MutableLiveData()
    val version: LiveData<Constants.ForzaVersion> get() = _version

    fun setForzaVersion(version: Constants.ForzaVersion) {
        _version.postValue(version)
    }

    fun setInetState(inet: ForzaListener.InetState) {
        if(inet.ipString.equals(Constants.DEFAULT_IP)){
            _inetError.postValue(true)
        } else {
            _inetError.postValue(false)
            _inetInfo.postValue(InetInfo(inet.ipString, inet.port))
        }
    }
}