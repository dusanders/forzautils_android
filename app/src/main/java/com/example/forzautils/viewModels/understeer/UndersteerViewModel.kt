package com.example.forzautils.viewModels.understeer

import androidx.lifecycle.ViewModel
import com.example.forzautils.utils.CanvasCoordinate
import com.example.forzautils.viewModels.interfaces.IForzaDataStream
import forza.telemetry.data.TelemetryData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

data class UndersteerEvent(
  val coordinates: List<CanvasCoordinate> = emptyList()
)

class UndersteerViewModel(
  forzaViewModel: IForzaDataStream
) : ViewModel() {
  private val tag = "UndersteerViewModel"
  private val windowSize = 200

  init {
    CoroutineScope(Dispatchers.Default).launch {
      forzaViewModel.data.collect {
        processPacket(it)
      }
    }
  }

  private fun processPacket(data: TelemetryData?) {

  }
}