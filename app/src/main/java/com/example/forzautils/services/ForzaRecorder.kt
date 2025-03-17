package com.example.forzautils.services

import android.content.Context
import android.util.Log
import com.example.forzautils.dataModels.RecordedSession
import forza.telemetry.data.TelemetryData
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.UUID
import kotlin.io.path.Path

interface IForzaRecorder {
  fun writePacket(data: TelemetryData?)
  fun getAllRecordings(): List<RecordedSession>
  fun prepareRecording()
  fun stopRecording()
  fun deleteRecording(session: RecordedSession)
  fun openRecording(session: RecordedSession): SessionFile?
}

class ForzaRecorder(val context: Context) : IForzaRecorder {
  private val tag = "ForzaRecorder"

  private val sessionsDatabase = RecordedSessionDatabase(context)
  private var preparedRecording: RecordedSession? = null
  private var currentRecording: SessionFile? = null
  private var currentReplay: SessionFile? = null

  override fun prepareRecording() {
    val completedRecording = currentRecording?.close()
    if(completedRecording != null) {
      sessionsDatabase.addSession(completedRecording)
    }
    val id = UUID.randomUUID().toString()
    val dateString = SimpleDateFormat(
      "yyyy-MM-dd-HH:mm",
      Locale.getDefault()
    ).format(
      System.currentTimeMillis()
    )
    var filepath = Path(context.dataDir.path, id).toString()
    preparedRecording = RecordedSession(
      id = id,
      filepath = filepath,
      date = dateString
    )
    currentRecording = SessionFile(
      recordedSession = preparedRecording!!,
      context = context
    )
  }

  override fun writePacket(data:TelemetryData?) {
    currentRecording?.writePacket(data)
  }

  override fun stopRecording() {
    val dbEntity = currentRecording?.close()
    if(dbEntity != null) {
      sessionsDatabase.addSession(dbEntity)
    }
    currentRecording = null
    preparedRecording = null
  }

  override fun getAllRecordings(): List<RecordedSession> {
    return sessionsDatabase.getAllSessions()
  }

  override fun deleteRecording(session: RecordedSession) {
    sessionsDatabase.deleteSession(session.id)
    val sessionFile = SessionFile(session, context)
    sessionFile.delete()
  }

  override fun openRecording(session: RecordedSession): SessionFile? {
    try{
      return SessionFile(session, context)
    }catch (e: Exception) {
      Log.w(tag, "Failed to open recording (${session.filepath}): ${e.message}")
    }
    return null
  }
}