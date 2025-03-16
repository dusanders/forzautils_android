package com.example.forzautils.viewModels.forzaViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.forzautils.services.ForzaRecorder
import com.example.forzautils.services.ForzaService

class ForzaViewModelFactory(
  private val forzaRecorder: ForzaRecorder,
  private val forzaService: ForzaService
) : ViewModelProvider.Factory {
  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    if (modelClass.isAssignableFrom(ForzaViewModel::class.java)) {
      @Suppress("UNCHECKED_CAST")
      return ForzaViewModel(forzaService, forzaRecorder) as T
    }
    throw IllegalArgumentException("Unknown ViewModel class")
  }
}