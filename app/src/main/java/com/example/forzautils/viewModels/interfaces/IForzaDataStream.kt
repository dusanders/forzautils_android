package com.example.forzautils.viewModels.interfaces

import forza.telemetry.data.TelemetryData
import kotlinx.coroutines.flow.StateFlow

interface IForzaDataStream {
  val data: StateFlow<TelemetryData?>
}