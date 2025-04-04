package com.example.forzautils.dataModels

import forza.telemetry.data.models.CarModel
import forza.telemetry.data.models.TrackModel

data class SessionInfo(
  val trackModel: TrackModel,
  val carModel: CarModel
)
