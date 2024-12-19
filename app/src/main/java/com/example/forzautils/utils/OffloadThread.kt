package com.example.forzautils.utils

import android.os.Handler
import android.os.HandlerThread
import android.util.Log

class OffloadThread private constructor() {
    companion object {
        private var _instance: OffloadThread = OffloadThread()
        fun Instance(): OffloadThread {
            return _instance;
        }
    }

    private val _tag = "OffloadThread"
    private var _handlerThread = HandlerThread(_tag)
    private var _handler: Handler

    init {
        Log.d(_tag, "create instance...")
        _handlerThread.start()
        _handler = Handler(_handlerThread.looper)
    }
    fun interrupt() {
        _handlerThread.interrupt()
    }
    fun post(runnable: Runnable) {
        if(_handlerThread.isInterrupted) {
            Log.d(_tag, "Handler thread is interrupted...")
            _handlerThread = HandlerThread(_tag)
            _handlerThread.start()
            _handler = Handler(_handlerThread.looper)
        }
        _handler.post(runnable)
    }
}