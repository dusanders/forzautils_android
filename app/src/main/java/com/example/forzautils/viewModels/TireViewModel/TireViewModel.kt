package com.example.forzautils.viewModels.TireViewModel

import androidx.lifecycle.ViewModel
import com.example.forzautils.utils.DataWindow
import com.example.forzautils.viewModels.interfaces.IForzaDataStream
import forza.telemetry.data.TelemetryData
import forza.telemetry.data.models.CarModel
import forza.telemetry.data.models.SpatialModel
import forza.telemetry.data.models.TireModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class TireDynamicsEvent(
  val steeringAngle: Float = 0f,
  val roll: Float = 0f,
  val pitch: Float = 0f,
  val yaw: Float = 0f,
  val temp: Float = 0f,
  val slip: Float = 0f,
  val combinedSlip: Float = 0f,
  val ratio: Float = 0f,
  val wear: Float = 0f,
)

class TireViewModel(
  forzaDataStream: IForzaDataStream
) : ViewModel() {
  private val tag = "TireViewModel"
  private val windowSize = 200

  private val tireModelsWindow = DataWindow<TireModel>(windowSize)
  val tireModels: StateFlow<List<TireModel>> get() = tireModelsWindow.window

  private val spatialModelsWindow = DataWindow<SpatialModel>(windowSize)
  val spatialModels: StateFlow<List<SpatialModel>> get() = spatialModelsWindow.window

  private val carModelsWindow = DataWindow<CarModel>(windowSize)
  val carModels: StateFlow<List<CarModel>> get() = carModelsWindow.window

  val leftFrontDynamicEvent = DataWindow<TireDynamicsEvent>(windowSize)
  val rightFrontDynamicEvent = DataWindow<TireDynamicsEvent>(windowSize)
  val leftRearDynamicEvent = DataWindow<TireDynamicsEvent>(windowSize)
  val rightRearDynamicEvent = DataWindow<TireDynamicsEvent>(windowSize)

  init {
    CoroutineScope(Dispatchers.Default).launch {
      forzaDataStream.data.collect {
        processPacket(it)
      }
    }
  }

  private fun processPacket(data: TelemetryData?) {
    if (data == null || !data.isRaceOn) {
      return
    }
    val tireModel = TireModel.fromTelemetryData(data)
    tireModelsWindow.add(tireModel)
    val spatialModel = SpatialModel.fromTelemetryData(data)
    spatialModelsWindow.add(spatialModel)
    val carModel = data.getCarInfo()
    carModelsWindow.add(carModel)
    leftFrontDynamicEvent.add(
      TireDynamicsEvent(
        steeringAngle = carModel.steer.toFloat(),
        roll = spatialModel.roll,
        pitch = spatialModel.pitch,
        yaw = spatialModel.yaw,
        temp = tireModel.tireTempFrontLeft,
        slip = tireModel.tireSlipAngleFrontLeft,
        combinedSlip = tireModel.tireCombinedSlipFrontLeft,
        wear = tireModel.tireWearFrontLeft,
        ratio = tireModel.tireSlipRatioFrontLeft
      )
    )
    rightFrontDynamicEvent.add(
      TireDynamicsEvent(
        steeringAngle = carModel.steer.toFloat(),
        roll = spatialModel.roll,
        pitch = spatialModel.pitch,
        yaw = spatialModel.yaw,
        temp = tireModel.tireTempFrontRight,
        slip = tireModel.tireSlipAngleFrontRight,
        combinedSlip = tireModel.tireCombinedSlipFrontRight,
        wear = tireModel.tireWearFrontRight,
        ratio = tireModel.tireSlipRatioFrontRight
      )
    )
    leftRearDynamicEvent.add(
      TireDynamicsEvent(
        steeringAngle = carModel.steer.toFloat(),
        roll = spatialModel.roll,
        pitch = spatialModel.pitch,
        yaw = spatialModel.yaw,
        temp = tireModel.tireTempRearLeft,
        slip = tireModel.tireSlipAngleRearLeft,
        combinedSlip = tireModel.tireCombinedSlipRearLeft,
        wear = tireModel.tireWearRearLeft,
        ratio = tireModel.tireSlipRatioRearLeft
      )
    )
    rightRearDynamicEvent.add(
      TireDynamicsEvent(
        steeringAngle = carModel.steer.toFloat(),
        roll = spatialModel.roll,
        pitch = spatialModel.pitch,
        yaw = spatialModel.yaw,
        temp = tireModel.tireTempRearRight,
        slip = tireModel.tireSlipAngleRearRight,
        combinedSlip = tireModel.tireCombinedSlipRearRight,
        wear = tireModel.tireWearRearRight,
        ratio = tireModel.tireSlipRatioRearRight
      )
    )
  }
}