package com.example.forzautils.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.forzautils.services.WiFiService

class NetworkInfoViewModelFactory(private val wifiService: WiFiService): ViewModelProvider.Factory {
  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    if(modelClass.isAssignableFrom(NetworkInfoViewModel::class.java)){
      @Suppress("UNCHECKED_CAST")
      return NetworkInfoViewModel(wifiService) as T
    }
    throw IllegalArgumentException("Unknown ViewModel class")
  }
}