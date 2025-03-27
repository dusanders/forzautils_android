package com.example.forzautils.viewModels.trackMap

import androidx.compose.ui.graphics.Path
import androidx.lifecycle.ViewModel
import com.example.forzautils.utils.CanvasCoordinate
import com.example.forzautils.utils.DataWindow
import com.example.forzautils.viewModels.interfaces.IForzaDataStream
import forza.telemetry.data.TelemetryData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class UndersteerEvent(
  var coordinates: List<CanvasCoordinate> = emptyList()
)

class TrackMapViewModel(
  private val forzaViewModel: IForzaDataStream
) : ViewModel() {
  private val tag = "TrackMapViewModel"
  private val understeerWindowSize = 200
  private val trackWindowSize = 5000
  private var isUndeersteering = false
  private var lastCanvasCenter: CanvasCoordinate = CanvasCoordinate(0f, 0f)

  val trackWindow = DataWindow<CanvasCoordinate>(trackWindowSize)
  val understeerWindow = DataWindow<UndersteerEvent>(understeerWindowSize)
  var absoluteStartPosition: CanvasCoordinate? = null

  private val _trackPath = MutableStateFlow(Path())
  val trackPath: StateFlow<Path> = _trackPath

  private val _trackTitle = MutableStateFlow("")
  val trackTitle: StateFlow<String> = _trackTitle

  private val _currentPosition = MutableStateFlow(CanvasCoordinate(0f, 0f))
  val currentPosition: StateFlow<CanvasCoordinate> = _currentPosition

  private val _currentScalar = MutableStateFlow(1f)
  val currentScalar: StateFlow<Float> = _currentScalar

  init {
    CoroutineScope(Dispatchers.Default).launch {
      forzaViewModel.data.collect { data ->
        processData(data)
      }
    }
  }

  fun redrawWithCenterAndScalar(canvasCenter: CanvasCoordinate, scalar: Float) {
    val newPath = Path()
    _currentScalar.value = scalar
    lastCanvasCenter = canvasCenter
    trackWindow.window.value.forEachIndexed { index, position ->
      if (index == 0) {
        newPath.moveTo(canvasCenter.x, canvasCenter.y)
      } else {
        val newPosition = normalizeToCanvas(position, canvasCenter, scalar)
        if (newPosition.y < 0) {
          return redrawWithCenterAndScalar(canvasCenter, scalar - 0.1f)
        }
        if (newPosition.x < 0) {
          return redrawWithCenterAndScalar(canvasCenter, scalar - 0.1f)
        }
        newPath.lineTo(newPosition.x, newPosition.y)
      }
    }
    _trackPath.value = newPath
    if (trackWindow.window.value.isNotEmpty()) {
      _currentPosition.value = normalizeToCanvas(
        trackWindow.window.value.last(),
        canvasCenter,
        scalar
      )
    }
  }

  private fun normalizeToCanvas(
    position: CanvasCoordinate,
    center: CanvasCoordinate,
    scalar: Float
  ): CanvasCoordinate {
    if (absoluteStartPosition == null) {
      return CanvasCoordinate(0f, 0f)
    }
    val relativeX = ((absoluteStartPosition!!.x * scalar) - (position.x * scalar))
    val relativeY = ((absoluteStartPosition!!.y * scalar) - (position.y * scalar))
    return CanvasCoordinate(
      center.x + relativeX,
      center.y + relativeY
    )
  }

  private fun processData(data: TelemetryData?) {
    if (data == null) {
      return
    }
    if (_trackTitle.value.isEmpty()) {
      parseTrackTitle(data)
    }
    checkUndersteering(data)
    updateCurrentPosition(data)
    appendTrackPosition(data)
    redrawWithCenterAndScalar(lastCanvasCenter, _currentScalar.value)
  }

  private fun appendTrackPosition(data: TelemetryData) {
    trackWindow.add(
      CanvasCoordinate(data.positionX, data.positionZ)
    )
  }

  private fun updateCurrentPosition(data: TelemetryData) {
    if (absoluteStartPosition == null) {
      absoluteStartPosition = CanvasCoordinate(data.positionX, data.positionZ)
    }
    _currentPosition.value = CanvasCoordinate(data.positionX, data.positionZ)
  }

  private fun parseTrackTitle(data: TelemetryData) {
    _trackTitle.value = data.getTrackInfo().getCircuit() +
        " " + data.getTrackInfo().getTrack()
  }

  private fun checkUndersteering(data: TelemetryData) {
    val avgFrontSlip = (data!!.tireSlipRatioFrontLeft + data.tireSlipRatioFrontRight) / 2
    if (avgFrontSlip >= 0.5) {
      if (!isUndeersteering) {
        addUndersteerEvent(data)
      } else {
        appendUndersteerEvent(data)
      }
    } else {
      isUndeersteering = false;
    }
  }

  private fun addUndersteerEvent(data: TelemetryData) {
    isUndeersteering = true;
    val newUndersteerEvent = UndersteerEvent(
      coordinates = listOf(
        CanvasCoordinate(data.positionX, data.positionZ)
      )
    )
    understeerWindow.add(newUndersteerEvent)
  }

  private fun appendUndersteerEvent(data: TelemetryData) {
    val lastEvent = understeerWindow.window.value.last()
    lastEvent.coordinates = lastEvent.coordinates.plus(
      CanvasCoordinate(data.positionX, data.positionZ)
    )
    understeerWindow.replaceLast(
      lastEvent
    )
  }
}