package com.example.forzautils.viewModels.replay

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.forzautils.dataModels.RecordedSession
import com.example.forzautils.dataModels.SessionInfo
import com.example.forzautils.services.IForzaRecorder
import com.example.forzautils.services.SessionFile
import com.example.forzautils.viewModels.interfaces.IForzaDataStream
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
        val packet = _currentSession.value?.readPacket(requestedOffset)
        if (packet != null) {
          viewModelScope.launch {
            _forzaData.emit(packet)
            requestedOffset = _currentSession.value?.readPacketCount ?: 0
            _packetReadCount.emit(requestedOffset)
          }
          delay(30)
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