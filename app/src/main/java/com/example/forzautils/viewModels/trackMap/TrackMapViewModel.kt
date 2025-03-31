package com.example.forzautils.viewModels.trackMap

import android.util.Log
import androidx.compose.ui.graphics.Path
import androidx.lifecycle.ViewModel
import com.example.forzautils.ui.components.trackMap.ScaledMap
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
  private var isUndersteering = false
  private var canvasHeight = 0f
  private var canvasWidth = 0f
  private var trackId: Int = -1
  private var scaledMap: ScaledMap? = null
  private var currentLap = -1

  val understeerWindow = DataWindow<UndersteerEvent>(understeerWindowSize)

  private val _trackPath = MutableStateFlow(Path())
  val trackPath: StateFlow<Path> = _trackPath

  private val _trackTitle = MutableStateFlow("")
  val trackTitle: StateFlow<String> = _trackTitle

  private val _currentPosition = MutableStateFlow<CanvasCoordinate?>(null)
  val currentPosition: StateFlow<CanvasCoordinate?> = _currentPosition

  private val _currentScalar = MutableStateFlow(1f)
  val currentScalar: StateFlow<Float> = _currentScalar

  init {
    Log.d(tag, "Initializing TrackMapViewModel")
    CoroutineScope(Dispatchers.Default).launch {
      forzaViewModel.data.collect { data ->
        processData(data)
      }
    }
  }

  fun setCanvasLayout(width: Float, height: Float) {
    if (canvasWidth != width && canvasHeight != height) {
      canvasWidth = width
      canvasHeight = height
      scaledMap = ScaledMap(width, height)
    }
  }

  private fun processData(data: TelemetryData?) {
    if (data == null) {
      return
    }
    if (!data.isRaceOn) {
      return
    }
    if (scaledMap == null) {
      return
    }
    if (data.trackID != trackId) {
      parseTrackTitle(data)
      trackId = data.trackID
      _trackPath.value = Path()
    }
    if (currentLap < 0) {
      currentLap = data.currentLap.toInt()
    }
    checkUndersteering(data)
    scaledMap!!.addPoint(
      CanvasCoordinate(data.positionX, data.positionZ),
      data.currentLap.toInt() != currentLap
    )
    _trackPath.value = scaledMap!!.getPath()
    _currentPosition.value = scaledMap!!.getCurrentPosition()
  }

  private fun parseTrackTitle(data: TelemetryData) {
    _trackTitle.value = data.getTrackInfo().getCircuit() +
        " " + data.getTrackInfo().getTrack()
  }

  private fun checkUndersteering(data: TelemetryData) {
    val avgFrontSlip = (data.tireSlipRatioFrontLeft + data.tireSlipRatioFrontRight) / 2
    if (avgFrontSlip >= 0.5) {
      if (!isUndersteering) {
        addUndersteerEvent(data)
      } else {
        appendUndersteerEvent(data)
      }
    } else {
      isUndersteering = false;
    }
  }

  private fun addUndersteerEvent(data: TelemetryData) {
    isUndersteering = true;
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