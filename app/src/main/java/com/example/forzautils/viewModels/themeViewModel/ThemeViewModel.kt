package com.example.forzautils.viewModels.themeViewModel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ThemeViewModelFactory(private val isDarkTheme: Boolean) : ViewModelProvider.Factory {
  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    if (modelClass.isAssignableFrom(ThemeViewModel::class.java)) {
      @Suppress("UNCHECKED_CAST")
      return ThemeViewModel(isDarkTheme) as T
    }
    throw IllegalArgumentException("Unknown ViewModel class")
  }
}

class ThemeViewModel(initialValue: Boolean) : ViewModel() {
  var _isDarkTheme = MutableStateFlow(initialValue)
  val isDarkTheme: StateFlow<Boolean> = _isDarkTheme

  fun setTheme(isDark: Boolean) {
    CoroutineScope(Dispatchers.Main).launch {
      Log.d("ThemeViewModel", "Setting theme to $isDark")
      _isDarkTheme.emit(isDark)
    }
  }
}