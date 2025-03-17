package com.example.forzautils.viewModels.replayViewModel

import android.util.Log
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

  private val _allSessions: MutableStateFlow<List<RecordedSession>> =
    MutableStateFlow(recorder.getAllRecordings())
  val allSessions: MutableStateFlow<List<RecordedSession>> get() = _allSessions

  private val _currentSession: MutableStateFlow<SessionFile?> =
    MutableStateFlow(null)
  val currentSession: MutableStateFlow<SessionFile?> get() = _currentSession

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
    allSessions.value = allSessions.value.filter { it.id != id }
    recorder.deleteRecording(getSession(id)!!)
    updateAllSessions()
  }

  private fun iterateReplayPackets() {
    Log.d(tag, "iterate replay packets")
    replayThread = CoroutineScope(Dispatchers.IO).launch {
      do {
        Log.d(tag, "iterate replay packets loop")
        val packet = _currentSession.value?.readPacket()
        Log.d(tag, "got packet: ${packet?.timeStampMS}")
//        Log.d(tag, "got packet: ${packet?.timeStampMS}")
        if (packet != null) {
          viewModelScope.launch {
            _forzaData.emit(packet)
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