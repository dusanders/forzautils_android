package com.example.forzautils.utils

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class DataWindow<T>(val windowSize: Int, val batchSize: Int = 1) {
  private val batchList = ArrayList<T>()
  private val _window = MutableStateFlow<List<T>>(emptyList())
  val window: StateFlow<List<T>> get() = _window

  fun addBatch(data: T) {
    batchList.add(data)
    var backing = window.value
    if(batchList.size == batchSize) {
      if(_window.value.size == windowSize
        || _window.value.size + batchSize > windowSize) {
        backing = backing.drop(batchSize)
      }
      _window.value = backing.plus(batchList)
      batchList.clear()
    }
  }

  fun add(data: T) {
    if(_window.value.size == windowSize) {
      _window.value = _window.value.drop(1)
    }
    _window.value = _window.value.plus(data)
  }
}