package com.example.forzautils.viewModels.forzaViewModel

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.forzautils.services.ForzaService
import forza.telemetry.data.TelemetryData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ForzaViewModel(private val forzaService: ForzaService): ViewModel() {

  private val _tag = "ForzaViewModel"
  private val _listening: MutableStateFlow<Int?> = MutableStateFlow(null)
  val listening: StateFlow<Int?> get() = _listening

  private val _data: MutableStateFlow<TelemetryData?> = MutableStateFlow(null)
  val data: StateFlow<TelemetryData?> get() = _data

  private val forzaListening: Observer<Int?> = Observer {
    viewModelScope.launch {
      _listening.emit(it)
    }
  }

  private val forzaData: Observer<TelemetryData?> = Observer {
    viewModelScope.launch {
      _data.emit(it)
    }
  }

  init {
    forzaService.forzaListening.observeForever(forzaListening)
    forzaService.data.observeForever(forzaData)
  }
}