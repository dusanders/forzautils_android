package com.example.forzautils.utils

import androidx.compose.runtime.mutableStateListOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class DataWindow<T>(val size: Int) {
  private val _window = MutableStateFlow<List<T>>(emptyList())
  val window: StateFlow<List<T>> get() = _window

  fun add(data: T) {
    if(_window.value.size == size) {
      _window.value = _window.value.drop(1)
    }
    _window.value = _window.value.plus(data)
  }
}