package com.example.forzautils.viewModels.hpTqRatings

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.ViewModel
import com.example.forzautils.viewModels.forzaViewModel.ForzaViewModel
import forza.telemetry.data.TelemetryData
import forza.telemetry.data.models.EngineModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HpTqRatingsViewModel(
  forzaViewModel: ForzaViewModel
) : ViewModel() {
  private val tag = "HpTqRatingsViewModel"
  private val RPM_STEP = 500
  val hpTqRatings = mutableStateMapOf<Int, Map<Int, EngineModel>>()

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
    val engineModel = EngineModel(data)
    val rpm = engineModel.getRoundedRpm()
    if (!isRpmStep(rpm)) {
      return
    }
    if (!hpTqRatings.containsKey(engineModel.gear)) {
      hpTqRatings[engineModel.gear] = mapOf(
        rpm to engineModel
      )
      return;
    }
    var existing = hpTqRatings[engineModel.gear]!!
    val hpTqEntry = existing[rpm]
    if (hpTqEntry != null) {
      if (hpTqEntry.power < engineModel.power
        || hpTqEntry.torque < engineModel.torque
      ) {
        existing = existing.minus(rpm)
        existing = existing.plus(rpm to engineModel)
      }
    } else {
      existing = existing.plus(rpm to engineModel)
    }
    hpTqRatings[engineModel.gear] = existing
  }

  private fun isRpmStep(rpm: Int): Boolean {
    return rpm % RPM_STEP == 0
  }
}