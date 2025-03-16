package com.example.forzautils.utils

import androidx.compose.runtime.mutableStateListOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class DataWindow<T>(val size: Int) {
  private val _window = mutableStateListOf<T>()
  val window: List<T> get() = _window

  fun add(data: T) {
    if(_window.size == size) {
      _window.removeAt(0)
    }
    _window.add(data)
  }
}