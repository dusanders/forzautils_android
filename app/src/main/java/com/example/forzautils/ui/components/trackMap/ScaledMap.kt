package com.example.forzautils.ui.components.trackMap

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Path
import com.example.forzautils.utils.CanvasCoordinate
import kotlin.math.abs

class ScaledMap(
  val width: Float,
  val height: Float
) {
  private val tag = "ScaledMap"
  private val padding = 30
  private val paddedHeight = height - (padding * 1)
  private val paddedWidth = width - (padding * 1)
  private var minX = Float.MAX_VALUE
  private var maxX = Float.MIN_VALUE
  private var minY = Float.MAX_VALUE
  private var maxY = Float.MIN_VALUE
  private var trackWidth = 1f
  private var trackHeight = 1f
  private var scaleX = 1f
  private var scaleY = 1f
  private var scale = 1f
  private var offsetX = 0f
  private var offsetY = 0f
  private val telemetryPoints = ArrayList<Pair<Float, Float>>()
  private var path = Path()

  init {
    Log.d(tag, "Initializing ScaledMap")
  }
  fun addPoint(point: CanvasCoordinate, hasCompletedLap: Boolean) {
    if(hasCompletedLap && isCoordinateCompleteLap(point)) {
      return
    }
    telemetryPoints.add(Pair(point.x, point.y))
    updateScales(point)
    redrawPath()
  }

  fun getCurrentPosition(): CanvasCoordinate {
    if(telemetryPoints.isEmpty()) {
      return CanvasCoordinate(0f, 0f)
    }
    return CanvasCoordinate(
      toCanvasX(telemetryPoints.last().first, scale, offsetX),
      toCanvasY(telemetryPoints.last().second, scale, offsetY)
    )
  }

  fun getPath(): Path {
    return path
  }

  // Convert X coordinate from track space to canvas space
  private fun toCanvasX(x: Float, scale: Float, offsetX: Float): Float {
    return (x - minX) * scale + offsetX // Apply scaling and center
  }

  // Convert Y coordinate from track space to canvas space
  private fun toCanvasY(y: Float, scale: Float, offsetY: Float): Float {
    return (maxY - y) * scale + offsetY
  }

  private fun redrawPath() {
    path = Path()
    if(telemetryPoints.size < 2) {
      return
    }

    // Convert the first point to the canvas coordinates
    val firstPoint = telemetryPoints.first()
    path.moveTo(
      toCanvasX(firstPoint.first, scale, offsetX),
      toCanvasY(firstPoint.second, scale, offsetY)
    )

    // Convert each telemetry point to canvas coordinates and draw
    for (point in telemetryPoints) {
      path.lineTo(
        toCanvasX(point.first, scale, offsetX),
        toCanvasY(point.second, scale, offsetY)
      )
    }
  }

  private fun updateScales(point: CanvasCoordinate): Boolean {
    var result = false;
    val tempMinX = minOf(minX, point.x)
    val tempMaxX = maxOf(maxX, point.x)
    val tempMinY = minOf(minY, point.y)
    val tempMaxY = maxOf(maxY, point.y)
    if(tempMinX != minX || tempMaxX != maxX || tempMinY != minY || tempMaxY != maxY) {
      minX = tempMinX
      maxX = tempMaxX
      minY = tempMinY
      maxY = tempMaxY
      result = true
      trackWidth = maxX - minX
      trackHeight = maxY - minY
      scaleX = paddedWidth / trackWidth
      scaleY = paddedHeight / trackHeight
      scale = minOf(scaleX, scaleY)
      offsetX = (paddedWidth - trackWidth * scale) / 2 + padding
      offsetY = (paddedHeight - trackHeight * scale) / 2 - padding
    }
    return result
  }

  private fun isCoordinateCompleteLap(point: CanvasCoordinate): Boolean {
    val overlapX = abs(point.x - telemetryPoints.first().first) < 1
    val overlapY = abs(point.y - telemetryPoints.first().second) < 1
    return overlapX && overlapY
  }
}