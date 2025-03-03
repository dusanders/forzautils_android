package com.example.forzautils.viewModels.networkInfo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.forzautils.services.ForzaService
import com.example.forzautils.services.WiFiService

class NetworkInfoViewModelFactory(
  private val wifiService: WiFiService,
  private val forzaService: ForzaService
): ViewModelProvider.Factory {
  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    if(modelClass.isAssignableFrom(NetworkInfoViewModel::class.java)){
      @Suppress("UNCHECKED_CAST")
      return NetworkInfoViewModel(wifiService, forzaService) as T
    }
    throw IllegalArgumentException("Unknown ViewModel class")
  }
}