package com.example.forzautils.viewModels.EngineInfo

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.forzautils.viewModels.interfaces.IForzaDataStream
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
  3 to mapOf(
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
  ),
)

class EngineInfoViewModel(
  forzaViewModel: IForzaDataStream
) : ViewModel() {
  private val tag = "HpTqRatingsViewModel"
  private val RPM_STEP = 50

  private val _minRpm = MutableStateFlow<Int>(0)
  val minRpm: StateFlow<Int> = _minRpm

  private val _maxRpm = MutableStateFlow<Int>(0)
  val maxRpm: StateFlow<Int> = _maxRpm

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
      if (_minRpm.value != engineModel.idleRpm.toInt()) {
        _minRpm.emit(engineModel.idleRpm.toInt())
      }
      if (_maxRpm.value != engineModel.maxRpm.toInt()) {
        _maxRpm.emit(engineModel.maxRpm.toInt())
      }
    }
  }

  private suspend fun updateGearMap(engineModel: EngineModel) {
    val rpm = engineModel.getRoundedRpm()
    if (!isRpmStep(rpm)) {
      return
    }
    if (engineModel.gear == 0 || engineModel.gear == 11) {
      return
    }
    if (!_powerMap.value.containsKey(engineModel.gear)) {
      Log.d(tag, "Adding new gear ${engineModel.gear}")
      return _powerMap.emit(
        _powerMap.value.plus(
          engineModel.gear
              to mapOf(
            rpm
                to engineModel
          ).toSortedMap()
        )
      )
    }

    var foundGearMap = _powerMap.value[engineModel.gear]

    if (!foundGearMap!!.containsKey(rpm)) {
      Log.d(tag, "Adding new rpm $rpm to gear ${engineModel.gear}")
      foundGearMap = foundGearMap.plus(rpm to engineModel)
      return _powerMap.emit(
        _powerMap.value
          .minus(engineModel.gear)
          .plus(engineModel.gear to foundGearMap)
      )
    }
    if (foundGearMap[rpm]!!.getHorsepower() < engineModel.getHorsepower()
      || foundGearMap[rpm]!!.torque < engineModel.torque
    ) {
      Log.d(tag, "Updating ${engineModel.gear} @ ${rpm} " +
          "with ${engineModel.power} ${engineModel.torque}")
      foundGearMap = foundGearMap
        .minus(rpm)
        .plus(rpm to engineModel)
        .toSortedMap()
    }
    return _powerMap.emit(
      _powerMap.value
        .minus(engineModel.gear)
        .plus(engineModel.gear to foundGearMap)
    )
  }

  private fun isRpmStep(rpm: Int): Boolean {
    return rpm % RPM_STEP == 0
  }
}