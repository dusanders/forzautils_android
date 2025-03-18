package com.example.forzautils.services

import android.content.Context
import android.util.Log
import com.example.forzautils.dataModels.RecordedSession
import com.example.forzautils.dataModels.SessionInfo
import forza.telemetry.data.TelemetryData
import forza.telemetry.data.TelemetryParser
import forza.telemetry.data.database.FM8DatabaseService
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.onClosed
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.RandomAccessFile
import java.nio.ByteBuffer
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class SessionFile(
  val recordedSession: RecordedSession,
  val context: Context
) {
  private val tag = "SessionFile"
  private var firstPacket: TelemetryData? = null

  var sessionInfo: SessionInfo? = null
  var totalPackets: Int = recordedSession.totalLen
  var readPacketCount: Int = 0
  private val file = File(recordedSession.filepath)
  private val writeChannel = Channel<ByteArray>(Channel.UNLIMITED)
  private var fileWriteThread: Job? = null
  private var exists: Boolean = file.exists()

  init {
    if (exists) {
      val firstPacketBuf = ByteBuffer.allocate(recordedSession.byteLen)
      val inputStream = FileInputStream(file)
      inputStream.read(
        firstPacketBuf.array(),
        0,
        recordedSession.byteLen
      )
      inputStream.close()
      firstPacket = TelemetryParser.Parse(
        recordedSession.byteLen,
        firstPacketBuf.array(),
        FM8DatabaseService(context)
      )
      sessionInfo = SessionInfo(
        firstPacket!!.getTrackInfo(),
        firstPacket!!.getCarInfo()
      )
    } else {
      file.createNewFile()
    }
  }

  suspend fun readPacket(offset: Int? = null): TelemetryData? = suspendCoroutine { continuation ->
    continuation.resumeWith(
      Result.success(
        readAndParseData(offset)
      )
    )
  }

  fun writePacket(data: TelemetryData?) {
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
    if (fileWriteThread == null) {
      startFileWriteThread()
    }
    CoroutineScope(Dispatchers.IO).launch {
      writeChannel.send(data.rawBytes)
      totalPackets++
      Log.d(tag, "wrote packet: ${recordedSession.byteLen} - ${totalPackets}")
    }

//    CoroutineScope(Dispatchers.IO).launch {
//      val outputStream = FileOutputStream(file, true)
//      outputStream.write(data.rawBytes.clone())
//      outputStream.flush()
//      outputStream.close()
//    }
//    totalPackets++;
//    Log.d(tag, "wrote packet: ${recordedSession.byteLen} - ${totalPackets}")
  }

  fun close(): RecordedSession {
    writeChannel.close()
    fileWriteThread?.cancel(CancellationException("User Stopped Recording"))
    return recordedSession.copy(totalLen = totalPackets)
  }

  fun delete() {
    file.delete()
  }

  private fun startFileWriteThread() {
    fileWriteThread = CoroutineScope(Dispatchers.IO).launch {
      do {
        val bytesToWrite = writeChannel.receiveCatching().onClosed {
          Log.d(tag, "write channel closed")
        }.getOrNull()
        if (bytesToWrite != null) {
          FileOutputStream(file, true).use { outputStream ->
            try {
              outputStream.write(bytesToWrite)
            } catch (e: Exception) {
              Log.w(tag, "Error writing to file: ${e.message}")
            }
          }
        }
      } while (bytesToWrite != null)
    }
  }

  private fun readAndParseData(offset: Int? = null): TelemetryData? {
    val offsetPacket = offset ?: readPacketCount
    val buf = ByteBuffer.allocate(recordedSession.byteLen)
    val buffreader = RandomAccessFile(file, "r")
    buffreader.seek(
      offsetPacket * recordedSession.byteLen.toLong()
    )
    val readLen = buffreader.read(
      buf.array(),
      0,
      recordedSession.byteLen
    )
    buffreader.close()
    // Update read packet count
    readPacketCount = offsetPacket + 1
    // EOF or incomplete packet - return null
    if (readLen != recordedSession.byteLen) {
      return null
    }
    return TelemetryParser.Parse(
      buf.array().size,
      buf.array(),
      FM8DatabaseService(context)
    )
  }
}