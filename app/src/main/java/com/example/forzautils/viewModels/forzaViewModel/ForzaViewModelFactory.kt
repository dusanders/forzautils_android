package com.example.forzautils.viewModels.forzaViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.forzautils.services.ForzaService

class ForzaViewModelFactory(private val forzaService: ForzaService): ViewModelProvider.Factory {
  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    if(modelClass.isAssignableFrom(ForzaViewModel::class.java)){
      @Suppress("UNCHECKED_CAST")
      return ForzaViewModel(forzaService) as T
    }
    throw IllegalArgumentException("Unknown ViewModel class")
  }
}