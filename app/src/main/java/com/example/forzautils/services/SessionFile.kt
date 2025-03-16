package com.example.forzautils.services

import android.content.Context
import com.example.forzautils.dataModels.RecordedSession
import com.example.forzautils.dataModels.SessionInfo
import forza.telemetry.data.TelemetryData
import forza.telemetry.data.TelemetryParser
import forza.telemetry.data.database.FM8DatabaseService
import java.io.File
import java.io.RandomAccessFile
import java.nio.ByteBuffer

class SessionFile(
  val recordedSession: RecordedSession,
  val context: Context
) {
  private val tag = "SessionFile(${recordedSession.filepath})"
  private val file = File(recordedSession.filepath)
  private val fileOutputStream = file.outputStream()
  private val fileInputStream = RandomAccessFile(file, "r")
  private var firstPacket: TelemetryData? = null

  var sessionInfo: SessionInfo? = null
  var totalPackets: Int = recordedSession.totalLen
  var readPacketCount: Int = 0
  val exists: Boolean = file.exists()

  init {
    if (exists) {
      val firstPacketBuf = ByteBuffer.allocate(recordedSession.byteLen)
      fileInputStream.read(
        firstPacketBuf.array(),
        0,
        recordedSession.byteLen
      )
      firstPacket = TelemetryParser.Parse(
        recordedSession.byteLen,
        firstPacketBuf.array(),
        FM8DatabaseService(context)
      )
      sessionInfo = SessionInfo(
        firstPacket!!.getTrackInfo(),
        firstPacket!!.getCarInfo()
      )
    }
  }

  fun readPacket(): TelemetryData? {
    val buf = ByteBuffer.allocate(recordedSession.byteLen)
    fileInputStream.skipBytes(readPacketCount * recordedSession.byteLen)
    fileInputStream.read(
      buf.array(),
      0,
      recordedSession.byteLen
    )
    readPacketCount++;
    return TelemetryParser.Parse(
      buf.array().size,
      buf.array(),
      FM8DatabaseService(context))
  }

  fun writePacket(data: TelemetryData) {
    if(firstPacket == null) {
      firstPacket = data
      sessionInfo = SessionInfo(
        firstPacket!!.getTrackInfo(),
        firstPacket!!.getCarInfo()
      )
      recordedSession.byteLen = data.rawBytes.size
    }
    fileOutputStream.write(data.rawBytes)
    totalPackets++;
  }

  fun close(): RecordedSession {
    fileOutputStream.flush()
    fileOutputStream.close()
    fileInputStream.close()
    return recordedSession.copy(totalLen = totalPackets)
  }

  fun delete() {
    file.delete()
  }
}