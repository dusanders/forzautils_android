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
    private val _handlerThread = HandlerThread(_tag)
    private var _handler: Handler

    init {
        Log.d(_tag, "create instance...")
        _handlerThread.start()
        _handler = Handler(_handlerThread.looper)
    }
    fun post(runnable: Runnable) {
        _handler.post(runnable)
    }
}