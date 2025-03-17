package com.example.forzautils.services

import android.content.Context
import android.util.Log
import com.example.forzautils.dataModels.RecordedSession
import com.example.forzautils.dataModels.SessionInfo
import forza.telemetry.data.TelemetryData
import forza.telemetry.data.TelemetryParser
import forza.telemetry.data.database.FM8DatabaseService
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.RandomAccessFile
import java.nio.ByteBuffer
import java.nio.channels.FileChannel.MapMode.READ_WRITE

class SessionFile(
  val recordedSession: RecordedSession,
  val context: Context
) {
  private val tag = "SessionFile"
  private val file = File(recordedSession.filepath)
  private var firstPacket: TelemetryData? = null

  var sessionInfo: SessionInfo? = null
  var totalPackets: Int = recordedSession.totalLen
  var readPacketCount: Int = 0
  val exists: Boolean = file.exists()

  init {
    if (exists) {
      val firstPacketBuf = ByteBuffer.allocate(recordedSession.byteLen)
      FileInputStream(file).read(
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
    if(readPacketCount == totalPackets) {
      return null
    }
    val buf = ByteBuffer.allocate(recordedSession.byteLen)
    val buffreader = RandomAccessFile(file, "r")
    buffreader.seek(readPacketCount * recordedSession.byteLen.toLong())
    buffreader.read(
      buf.array(),
      0,
      recordedSession.byteLen
    )
    buffreader.close()
    readPacketCount++;
    return TelemetryParser.Parse(
      buf.array().size,
      buf.array(),
      FM8DatabaseService(context)
    )
  }

  fun writePacket(data: TelemetryData?) {
    if (!exists) {
      file.createNewFile()
    }
    if (data == null) {
      return
    }
    if (firstPacket == null) {
      firstPacket = data
      sessionInfo = SessionInfo(
        firstPacket!!.getTrackInfo(),
        firstPacket!!.getCarInfo()
      )
    }
    recordedSession.byteLen = data.rawBytes.size
    val outputStream = FileOutputStream(file, true)
    outputStream.write(data.rawBytes.clone())
    outputStream.close()
    totalPackets++;
    Log.d(tag, "wrote packet: ${recordedSession.byteLen} - ${totalPackets}")
  }

  fun close(): RecordedSession {
    return recordedSession.copy(totalLen = totalPackets)
  }

  fun delete() {
    file.delete()
  }
}