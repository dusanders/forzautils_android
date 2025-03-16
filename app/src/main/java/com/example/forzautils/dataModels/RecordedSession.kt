package com.example.forzautils.dataModels

data class RecordedSession(
  val id: String,
  val filepath: String,
  val date: String,
  var byteLen: Int = 0,
  var totalLen: Int = 0
)
