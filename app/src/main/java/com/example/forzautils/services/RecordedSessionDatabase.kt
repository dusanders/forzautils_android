package com.example.forzautils.services

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.provider.BaseColumns
import android.util.Log
import com.example.forzautils.dataModels.RecordedSession

class RecordedSessionDatabase(val context: Context) {
  companion object {
    private const val TAG = "RecordedSessionDatabase"
    private const val DATABASE_NAME = "recorded_sessions.db"
  }

  object Session : BaseColumns {
    const val TABLE_NAME = "sessions"
    const val COLUMN_ID = "id"
    const val COLUMN_FILENAME = "filename"
    const val COLUMN_DATE = "date"
    const val COLUMN_BYTE_LEN = "byte_len"
    const val COLUMN_TOTAL_LEN = "total_len"
  }

  private val dbPath = context.getDatabasePath(DATABASE_NAME).path

  init {
    Log.d(TAG, "Using DB path: $dbPath")
    val sqlDb = SQLiteDatabase.openOrCreateDatabase(
      dbPath, null
    )
    sqlDb.execSQL(
      "CREATE TABLE IF NOT EXISTS ${Session.TABLE_NAME} (" +
          "${Session.COLUMN_ID} TEXT PRIMARY KEY, " +
          "${Session.COLUMN_FILENAME} TEXT, " +
          "${Session.COLUMN_DATE} TEXT, " +
          "${Session.COLUMN_BYTE_LEN} INTEGER, " +
          "${Session.COLUMN_TOTAL_LEN} INTEGER)"
    )
    sqlDb.close()
    debugPrintSessions()
  }

  fun findSession(id: String): RecordedSession? {
    val sqlDb = SQLiteDatabase.openOrCreateDatabase(
      dbPath, null
    )
    val session = sqlDb.query(
      Session.TABLE_NAME,
      arrayOf(
        Session.COLUMN_ID,
        Session.COLUMN_FILENAME,
        Session.COLUMN_DATE,
        Session.COLUMN_BYTE_LEN,
        Session.COLUMN_TOTAL_LEN
      ),
      "${Session.COLUMN_ID} = ?",
      arrayOf(id),
      null,
      null,
      null
    )
    if (session.moveToFirst()) {
      val result = RecordedSession(
        id = session.getString(session.getColumnIndexOrThrow(Session.COLUMN_ID)),
        filepath = session.getString(session.getColumnIndexOrThrow(Session.COLUMN_FILENAME)),
        date = session.getString(session.getColumnIndexOrThrow(Session.COLUMN_DATE)),
        byteLen = session.getInt(session.getColumnIndexOrThrow(Session.COLUMN_BYTE_LEN)),
        totalLen = session.getInt(session.getColumnIndexOrThrow(Session.COLUMN_TOTAL_LEN))
      )
      session.close()
      sqlDb.close()
      return result
    } else {
      session.close()
      sqlDb.close()
      return null
    }
  }

  fun deleteSession(id: String) {
    val sqlDb = SQLiteDatabase.openOrCreateDatabase(
      dbPath, null
    )
    sqlDb.delete(
      Session.TABLE_NAME,
      "${Session.COLUMN_ID} = ?",
      arrayOf(id)
    )
    sqlDb.close()
  }

  fun addSession(session: RecordedSession) {
    val sqlDb = SQLiteDatabase.openOrCreateDatabase(
      dbPath, null
    )
    val contentValues = ContentValues().apply {
      put(Session.COLUMN_ID, session.id)
      put(Session.COLUMN_FILENAME, session.filepath)
      put(Session.COLUMN_DATE, session.date)
      put(Session.COLUMN_BYTE_LEN, session.byteLen)
      put(Session.COLUMN_TOTAL_LEN, session.totalLen)
    }
    Log.d(TAG, "adding session: $contentValues")
    sqlDb.insert(
      Session.TABLE_NAME,
      null,
      contentValues
    )
    sqlDb.close()
  }

  fun getAllSessions(): List<RecordedSession> {
    val result = ArrayList<RecordedSession>()
    val sqlDb = SQLiteDatabase.openOrCreateDatabase(
      dbPath, null
    )
    val allSessions = sqlDb.query(
      Session.TABLE_NAME,
      arrayOf(
        Session.COLUMN_ID,
        Session.COLUMN_FILENAME,
        Session.COLUMN_DATE,
        Session.COLUMN_BYTE_LEN,
        Session.COLUMN_TOTAL_LEN
      ),
      null,
      null,
      null,
      null,
      null
    )
    if (allSessions.moveToFirst()) {
      do {
        result.add(RecordedSession(
          id = allSessions.getString(allSessions.getColumnIndexOrThrow(Session.COLUMN_ID)),
          filepath = allSessions.getString(allSessions.getColumnIndexOrThrow(Session.COLUMN_FILENAME)),
          date = allSessions.getString(allSessions.getColumnIndexOrThrow(Session.COLUMN_DATE)),
          byteLen = allSessions.getInt(allSessions.getColumnIndexOrThrow(Session.COLUMN_BYTE_LEN)),
          totalLen = allSessions.getInt(allSessions.getColumnIndexOrThrow(Session.COLUMN_TOTAL_LEN))
        ))
      }while (allSessions.moveToNext())
    }
    allSessions.close()
    sqlDb.close()
    return result
  }

  private fun debugPrintSessions() {
    val sqlDb = SQLiteDatabase.openOrCreateDatabase(
      dbPath, null
    )
    val allSessions = sqlDb.query(
      Session.TABLE_NAME,
      arrayOf(
        Session.COLUMN_ID,
        Session.COLUMN_FILENAME,
        Session.COLUMN_DATE,
        Session.COLUMN_BYTE_LEN,
        Session.COLUMN_TOTAL_LEN
      ),
      null,
      null,
      null,
      null,
      null
    )
    if (allSessions.moveToFirst()) {
      do {
        val id = allSessions.getString(allSessions.getColumnIndexOrThrow(Session.COLUMN_ID))
        val filename =
          allSessions.getString(allSessions.getColumnIndexOrThrow(Session.COLUMN_FILENAME))
        val date = allSessions.getString(allSessions.getColumnIndexOrThrow(Session.COLUMN_DATE))
        val totalLen = allSessions.getLong(allSessions.getColumnIndexOrThrow(Session.COLUMN_TOTAL_LEN))
        Log.d(TAG, "Found session: $id $filename $date $totalLen")
      } while (allSessions.moveToNext())
    } else  {
      Log.d(TAG, "No sessions found")
    }
    allSessions.close()
    sqlDb.close()
  }
}