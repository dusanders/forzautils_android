package com.example.forzautils.viewModels.TireViewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.forzautils.utils.DataWindow
import com.example.forzautils.viewModels.interfaces.IForzaDataStream
import forza.telemetry.data.TelemetryData
import forza.telemetry.data.models.TireModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class TireViewModel(
  forzaDataStream: IForzaDataStream
): ViewModel() {
  private val tag = "TireViewModel"
  private val windowSize = 20
  private val leftFrontTempWindow = DataWindow<Float>(windowSize)
  val leftFrontTemp: StateFlow<List<Float>> get() = leftFrontTempWindow.window
  private val rightFrontTempWindow = DataWindow<Float>(windowSize)
  val rightFrontTemp: StateFlow<List<Float>> get() = rightFrontTempWindow.window
  private val leftRearTempWindow = DataWindow<Float>(windowSize)
  val leftRearTemp: StateFlow<List<Float>> get() = leftRearTempWindow.window
  private val rightRearTempWindow = DataWindow<Float>(windowSize)
  val rightRearTemp: StateFlow<List<Float>> get() = rightRearTempWindow.window

  init {
    CoroutineScope(Dispatchers.Default).launch {
      forzaDataStream.data.collect {
        processPacket(it)
      }
    }
  }

  private fun processPacket(data: TelemetryData?) {
    if(data == null || !data.isRaceOn) {
      return
    }
//    Log.d(tag, "Processing packet ${data?.tireTempFrontLeft}")
    val tireModel = TireModel.fromTelemetryData(data)
    leftFrontTempWindow.add(tireModel.tireTempFrontLeft)
    rightFrontTempWindow.add(tireModel.tireTempFrontRight)
    leftRearTempWindow.add(tireModel.tireTempRearLeft)
    rightRearTempWindow.add(tireModel.tireTempRearRight)
//    Log.d(tag, "Left Front Temp: ${leftFrontTempWindow.window.value.size}")
  }
}