package com.example.forzautils.viewModels.replay

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.forzautils.dataModels.RecordedSession
import com.example.forzautils.dataModels.SessionInfo
import com.example.forzautils.services.IForzaRecorder
import com.example.forzautils.services.SessionFile
import com.example.forzautils.viewModels.engineInfo.EngineInfoViewModel
import com.example.forzautils.viewModels.interfaces.IForzaDataStream
import com.example.forzautils.viewModels.suspension.SuspensionViewModel
import com.example.forzautils.viewModels.tire.TireViewModel
import com.example.forzautils.viewModels.trackMap.TrackMapViewModel
import forza.telemetry.data.TelemetryData
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ReplayViewModel(
  private val recorder: IForzaRecorder
) : ViewModel(), IForzaDataStream {

  private val tag = "ReplayViewModel"
  private var replayThread: Job? = null
  private var requestedOffset: Int = 0
  private var replaySpeedMS: Long = 1500 / 60

  private val _allSessions: MutableStateFlow<List<RecordedSession>> =
    MutableStateFlow(recorder.getAllRecordings())
  val allSessions: MutableStateFlow<List<RecordedSession>> get() = _allSessions

  private val _currentSession: MutableStateFlow<SessionFile?> =
    MutableStateFlow(null)
  val currentSession: MutableStateFlow<SessionFile?> get() = _currentSession

  private val _packetReadCount: MutableStateFlow<Int> =
    MutableStateFlow(0)
  val packetReadCount: MutableStateFlow<Int> get() = _packetReadCount

  private val _forzaData: MutableStateFlow<TelemetryData?> =
    MutableStateFlow(null)
  override val data: StateFlow<TelemetryData?> get() = _forzaData

  var engineInfoViewModel: EngineInfoViewModel = EngineInfoViewModel(this)
  var tireViewModel: TireViewModel = TireViewModel(this)
  var trackMapViewModel: TrackMapViewModel = TrackMapViewModel(this)
  var suspensionViewModel: SuspensionViewModel = SuspensionViewModel(this)

  init {
    updateAllSessions()
  }

  fun openReplaySession(session: RecordedSession): Boolean {
    val sessionFile = recorder.openRecording(session)
    viewModelScope.launch {
      _currentSession.emit(sessionFile)
    }
    return sessionFile != null
  }

  fun startReplay() {
    iterateReplayPackets()
  }

  fun stopReplay() {
    _currentSession.value?.close()
    requestedOffset = 0

    engineInfoViewModel = EngineInfoViewModel(this)
    tireViewModel = TireViewModel(this)
    trackMapViewModel = TrackMapViewModel(this)
    suspensionViewModel = SuspensionViewModel(this)
    replayThread?.cancel(CancellationException("User stopped replay"))
  }

  fun getSession(id: String): RecordedSession? {
    return allSessions.value.find { it.id == id }
  }

  fun getSessionInfo(session: RecordedSession): SessionInfo? {
    val sessionFile = recorder.openRecording(session)
    return sessionFile?.sessionInfo
  }

  fun deleteSession(id: String) {
    recorder.deleteRecording(getSession(id)!!)
    updateAllSessions()
  }

  fun replayAtOffset(offset: Int) {
    requestedOffset = offset
    if(replayThread?.isActive?.not() != false) {
      iterateReplayPackets()
    }
  }

  private fun iterateReplayPackets() {
    replayThread = CoroutineScope(Dispatchers.IO).launch {
      do {
        val packet = _currentSession.value?.readPacket(true,requestedOffset)
        if (packet != null) {
          if(!packet.isRaceOn){
            Log.d(tag, "GAME PAUSED")
          }
          if(packet.isRaceOn) {
            viewModelScope.launch {
              _forzaData.emit(packet)
              requestedOffset = _currentSession.value?.readPacketCount ?: 0
              _packetReadCount.emit(requestedOffset)
            }
          }
          delay(replaySpeedMS)
        }
      } while (packet != null)
    }
  }

  fun updateAllSessions() {
    viewModelScope.launch {
      var result = recorder.getAllRecordings()
      if (result.isEmpty()) {
        result = listOf(
          RecordedSession(
            id = "0",
            filepath = "0",
            date = "DATE",
            byteLen = 331,
          )
        )
      }
      _allSessions.emit(result)
    }
  }
}