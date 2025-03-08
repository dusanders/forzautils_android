package com.example.forzautils.viewModels.EngineInfo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.forzautils.viewModels.forzaViewModel.ForzaViewModel
import forza.telemetry.data.ForzaConstants
import forza.telemetry.data.TelemetryData
import forza.telemetry.data.models.EngineModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

val debugGearMap = mapOf(
  1 to mapOf(
    1000 to EngineModel(
      currentRpm = 1000f,
      maxRpm = 9000f,
      idleRpm = 800f,
      power = 123f,
      torque = 134f,
    ),
    1500 to EngineModel(
      currentRpm = 1500f,
      maxRpm = 9000f,
      idleRpm = 800f,
      power = 126f,
      torque = 138f,
    ),
    2000 to EngineModel(
      currentRpm = 2000f,
      maxRpm = 9000f,
      idleRpm = 800f,
      power = 129f,
      torque = 141f,
    ),
    2500 to EngineModel(
      currentRpm = 2500f,
      maxRpm = 9000f,
      idleRpm = 800f,
      power = 132f,
      torque = 138f,
    ),
    3000 to EngineModel(
      currentRpm = 3000f,
      maxRpm = 9000f,
      idleRpm = 800f,
      power = 135f,
      torque = 124f,
    )
  ),
  2 to mapOf(
    1000 to EngineModel(
      currentRpm = 1000f,
      maxRpm = 9000f,
      idleRpm = 800f,
      power = 123f,
      torque = 134f,
    ),
    1500 to EngineModel(
      currentRpm = 1500f,
      maxRpm = 9000f,
      idleRpm = 800f,
      power = 126f,
      torque = 138f,
    ),
    2000 to EngineModel(
      currentRpm = 2000f,
      maxRpm = 9000f,
      idleRpm = 800f,
      power = 129f,
      torque = 141f,
    ),
    2500 to EngineModel(
      currentRpm = 2500f,
      maxRpm = 9000f,
      idleRpm = 800f,
      power = 132f,
      torque = 138f,
    ),
    3000 to EngineModel(
      currentRpm = 3000f,
      maxRpm = 9000f,
      idleRpm = 800f,
      power = 135f,
      torque = 124f,
    )
  )
)
class EngineInfoViewModel(
  forzaViewModel: ForzaViewModel
) : ViewModel() {
  private val tag = "HpTqRatingsViewModel"
  private val RPM_STEP = 500

  private val _gear = MutableStateFlow<Int>(0)
  val gear: StateFlow<Int> = _gear

  private val _rpm = MutableStateFlow<Int>(0)
  val rpm: StateFlow<Int> = _rpm

  private val _throttle = MutableStateFlow<Int>(0)
  val throttle: StateFlow<Int> = _throttle

  private val _powerMap = MutableStateFlow(debugGearMap)
  val powerMap: StateFlow<Map<Int, Map<Int, EngineModel>>> = _powerMap

  init {
    CoroutineScope(Dispatchers.Default).launch {
      forzaViewModel.data.collect {
        processData(it)
      }
    }
  }

  private fun processData(data: TelemetryData?) {
    if (data == null) {
      return
    }
    val engineModel = EngineModel.fromTelemetryData(data)
    viewModelScope.launch {
      updateGearMap(engineModel)
      val rpm = engineModel.getRoundedRpm()
      _gear.emit(engineModel.gear)
      _rpm.emit(rpm)
      _throttle.emit(engineModel.throttle)
    }
  }

  private suspend fun updateGearMap(engineModel: EngineModel) {
    val rpm = engineModel.getRoundedRpm()
    if (!isRpmStep(rpm)) {
      return
    }
    if (!_powerMap.value.containsKey(engineModel.gear)) {
      return _powerMap.emit(
        _powerMap.value.plus(engineModel.gear to mapOf(rpm to engineModel))
      )
    }
    var foundGearMap = _powerMap.value[engineModel.gear]
    if (!foundGearMap!!.containsKey(rpm)) {
      foundGearMap = foundGearMap.plus(rpm to engineModel)
      return _powerMap.emit(_powerMap.value.plus(engineModel.gear to foundGearMap))
    }
    if (foundGearMap[rpm]!!.power < engineModel.power
      || foundGearMap[rpm]!!.torque < engineModel.torque
    ) {
      foundGearMap = foundGearMap.minus(rpm)
      foundGearMap = foundGearMap.plus(rpm to engineModel)
      return _powerMap.emit(_powerMap.value.plus(engineModel.gear to foundGearMap))
    }
  }

  private fun isRpmStep(rpm: Int): Boolean {
    return rpm % RPM_STEP == 0
  }
}