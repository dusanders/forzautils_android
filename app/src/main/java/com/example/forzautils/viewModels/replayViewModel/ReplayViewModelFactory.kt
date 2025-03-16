package com.example.forzautils.viewModels.replayViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.forzautils.services.IForzaRecorder

class ReplayViewModelFactory(
  private val recorder: IForzaRecorder
): ViewModelProvider.Factory {
  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    if(modelClass.isAssignableFrom(ReplayViewModel::class.java)) {
      @Suppress("UNCHECKED_CAST")
      return ReplayViewModel(recorder) as T
    }
    throw IllegalArgumentException("Unknown ViewModel class")
  }
}