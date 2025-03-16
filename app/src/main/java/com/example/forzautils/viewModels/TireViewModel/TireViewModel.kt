package com.example.forzautils.viewModels.TireViewModel

import com.example.forzautils.utils.DataWindow
import com.example.forzautils.viewModels.interfaces.IForzaDataStream
import forza.telemetry.data.TelemetryData
import forza.telemetry.data.models.TireModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class TireViewModel(
  forzaDataStream: IForzaDataStream
) {
  private val tag = "TireViewModel"
  private val windowSize = 20
  private val leftFrontTempWindow = DataWindow<Float>(windowSize)
  val leftFrontTemp: List<Float> get() = leftFrontTempWindow.window
  private val rightFrontTempWindow = DataWindow<Float>(windowSize)
  val rightFrontTemp: List<Float> get() = rightFrontTempWindow.window
  private val leftRearTempWindow = DataWindow<Float>(windowSize)
  val leftRearTemp: List<Float> get() = leftRearTempWindow.window
  private val rightRearTempWindow = DataWindow<Float>(windowSize)
  val rightRearTemp: List<Float> get() = rightRearTempWindow.window
  private val leftFrontSlipAngleWindow = DataWindow<Float>(windowSize)
  val leftFrontSlipAngle: List<Float> get() = leftFrontSlipAngleWindow.window
  private val rightFrontSlipAngleWindow = DataWindow<Float>(windowSize)
  val rightFrontSlipAngle: List<Float> get() = rightFrontSlipAngleWindow.window
  private val leftRearSlipAngleWindow = DataWindow<Float>(windowSize)
  val leftRearSlipAngle: List<Float> get() = leftRearSlipAngleWindow.window
  private val rightRearSlipAngleWindow = DataWindow<Float>(windowSize)
  val rightRearSlipAngle: List<Float> get() = rightRearSlipAngleWindow.window

  init {
    CoroutineScope(Dispatchers.Default).launch {
      forzaDataStream.data.collect {
        processPacket(it)
      }
    }
  }

  private fun processPacket(data: TelemetryData?) {
    val tireModel = TireModel.fromTelemetryData(data)
    leftFrontTempWindow.add(tireModel.tireTempFrontLeft)
    rightFrontTempWindow.add(tireModel.tireTempFrontRight)
    leftRearTempWindow.add(tireModel.tireTempRearLeft)
    rightRearTempWindow.add(tireModel.tireTempRearRight)
    leftFrontSlipAngleWindow.add(tireModel.tireSlipAngleFrontLeft)
    rightFrontSlipAngleWindow.add(tireModel.tireSlipAngleFrontRight)
    leftRearSlipAngleWindow.add(tireModel.tireSlipAngleRearLeft)
    rightRearSlipAngleWindow.add(tireModel.tireSlipAngleRearRight)
  }
}