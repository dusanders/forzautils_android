package com.example.forzautils.viewModels.forzaViewModel

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.forzautils.services.ForzaRecorder
import com.example.forzautils.services.ForzaService
import com.example.forzautils.viewModels.interfaces.IForzaDataStream
import forza.telemetry.data.TelemetryData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ForzaViewModel(
  private val forzaService: ForzaService,
  private val forzaRecorder: ForzaRecorder
) : ViewModel(), IForzaDataStream {
  private val _tag = "ForzaViewModel"

  private val _recording: MutableStateFlow<Boolean> = MutableStateFlow(false)
  val recording: StateFlow<Boolean> get() = _recording

  private val _listening: MutableStateFlow<Int?> = MutableStateFlow(null)
  val listening: StateFlow<Int?> get() = _listening

  private val _data: MutableStateFlow<TelemetryData?> = MutableStateFlow(null)
  override val data: StateFlow<TelemetryData?> get() = _data

  private val forzaListening: Observer<Int?> = Observer {
    viewModelScope.launch {
      _listening.emit(it)
    }
  }

  private val forzaData: Observer<TelemetryData?> = Observer {
    forzaRecorder.writePacket(it)
    viewModelScope.launch {
      _data.emit(it)
    }
  }

  init {
    forzaService.forzaListening.observeForever(forzaListening)
    forzaService.data.observeForever(forzaData)
  }

  fun startRecording() {
    if (_recording.value) {
      return
    }
    forzaRecorder.prepareRecording()
    viewModelScope.launch {
      _recording.emit(true)
    }
  }

  fun stopRecording() {
    if (!_recording.value) {
      return
    }
    viewModelScope.launch {
      _recording.emit(false)
      forzaRecorder.stopRecording()
    }
  }
}