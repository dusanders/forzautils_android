package com.example.forzautils.viewModels.suspension

import androidx.lifecycle.ViewModel
import com.example.forzautils.utils.DataWindow
import com.example.forzautils.viewModels.interfaces.IForzaDataStream
import forza.telemetry.data.TelemetryData
import forza.telemetry.data.models.SuspensionModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SuspensionViewModel(
  forzaViewModel: IForzaDataStream
) : ViewModel() {
  private val tag = "SuspensionViewModel"
  private val windowSize = 200

  private val _frontDiff = DataWindow<Float>(windowSize)
  val frontDiff: DataWindow<Float>
    get() = _frontDiff

  private val _averageFrontSuspensionTravel = DataWindow<Float>(windowSize)
  val averageFrontSuspensionTravel: DataWindow<Float>
    get() = _averageFrontSuspensionTravel

  private val _rearDiff = DataWindow<Float>(windowSize)
  val rearDiff: DataWindow<Float>
    get() = _rearDiff

  private val _averageRearSuspensionTravel = DataWindow<Float>(windowSize)
  val averageRearSuspensionTravel: DataWindow<Float>
    get() = _averageRearSuspensionTravel

  private val _averageSuspensionDifference = DataWindow<Float>(windowSize)
  val averageSuspensionDifference: DataWindow<Float>
    get() = _averageSuspensionDifference

  private val _suspensionWindow = DataWindow<SuspensionModel>(windowSize)
  val suspensionWindow: DataWindow<SuspensionModel>
    get() = _suspensionWindow

  init {
    CoroutineScope(Dispatchers.Default).launch {
      forzaViewModel.data.collect { data ->
        processData(data)
      }
    }
  }

  private fun processData(data: TelemetryData?) {
    if (data == null) {
      return
    }
    if (!data.isRaceOn) {
      return
    }
    val suspensionModel = SuspensionModel.fromTelemetryData(data)
    _suspensionWindow.add(suspensionModel)
    val avgFront = (suspensionModel.normalizedSuspensionTravelFrontLeft
        + suspensionModel.normalizedSuspensionTravelFrontRight) / 2
    val avgRear = (suspensionModel.normalizedSuspensionTravelRearLeft
        + suspensionModel.normalizedSuspensionTravelRearRight) / 2
    val avgDiff = avgFront - avgRear
    val frontDiff = (suspensionModel.normalizedSuspensionTravelFrontLeft
        - suspensionModel.normalizedSuspensionTravelFrontRight)
    val rearDiff = (suspensionModel.normalizedSuspensionTravelRearLeft
        - suspensionModel.normalizedSuspensionTravelRearRight)
    _frontDiff.add(frontDiff)
    _rearDiff.add(rearDiff)
    _averageSuspensionDifference.add(avgDiff)
    _averageFrontSuspensionTravel.add(avgFront)
    _averageRearSuspensionTravel.add(avgRear)
  }
}