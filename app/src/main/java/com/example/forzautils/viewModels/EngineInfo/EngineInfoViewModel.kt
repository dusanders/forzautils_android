package com.example.forzautils.viewModels.engineInfo

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
  private val tag = "EngineInfoViewModel"

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

  private val _powerMap = MutableStateFlow<Map<Int, Map<Int, EngineModel>>>(debugGearMap)
  val powerMap: StateFlow<Map<Int, Map<Int, EngineModel>>> = _powerMap

  private val _power = MutableStateFlow<Float>(0f)
  val power: StateFlow<Float> = _power

  private val _torque = MutableStateFlow<Float>(0f)
  val torque: StateFlow<Float> = _torque

  init {
    CoroutineScope(Dispatchers.Default).launch {
      forzaViewModel.data.collect {
        processData(it)
      }
    }
  }

  private fun processData(data: TelemetryData?) {
    if (data == null || data.gear == 0 || data.gear == 11) {
      return
    }
    val engineModel = EngineModel.fromTelemetryData(data)
    viewModelScope.launch {
      _powerMap.emit(updateGearMap(engineModel))
      val rpm = engineModel.getRoundedRpm()
      _gear.emit(engineModel.gear)
      _rpm.emit(rpm)
      _throttle.emit(engineModel.throttle)
      _power.emit(engineModel.getHorsepower())
      _torque.emit(engineModel.torque)
      if (_minRpm.value != engineModel.idleRpm.toInt()) {
        _minRpm.emit(engineModel.idleRpm.toInt())
      }
      if (_maxRpm.value != engineModel.maxRpm.toInt()) {
        _maxRpm.emit(engineModel.maxRpm.toInt())
      }
    }
  }

  private fun updateGearMap(engineModel: EngineModel): Map<Int, Map<Int, EngineModel>> {
    val rpm = engineModel.getRoundedRpm()
    if (engineModel.gear == 0 || engineModel.gear == 11) {
      return _powerMap.value
    }
    if(engineModel.power < 0 || engineModel.torque < 0) {
      Log.d(tag, "power or torque is negative")
      return _powerMap.value
    }
    if (!_powerMap.value.containsKey(engineModel.gear)) {
      val newMap = _powerMap.value.plus(
        engineModel.gear
            to mapOf(
          rpm
              to engineModel
        ).toSortedMap()
      )
      return newMap
    }

    var foundGearMap = _powerMap.value[engineModel.gear]

    if (!foundGearMap!!.containsKey(rpm)) {
      foundGearMap = foundGearMap.plus(rpm to engineModel)
    }
    else if (foundGearMap[rpm]!!.power < engineModel.power
      || foundGearMap[rpm]!!.torque < engineModel.torque
    ) {
      foundGearMap = foundGearMap
        .minus(rpm)
        .plus(rpm to engineModel)
    }
    return _powerMap.value.minus(engineModel.gear)
      .plus(engineModel.gear to foundGearMap.toSortedMap())
  }

}