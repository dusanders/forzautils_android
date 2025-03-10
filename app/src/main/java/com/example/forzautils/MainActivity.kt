package com.example.forzautils

import android.app.UiModeManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.example.forzautils.services.ForzaService
import com.example.forzautils.services.ForzaServiceCallbacks
import com.example.forzautils.services.WiFiService
import com.example.forzautils.ui.ForzaApp
import com.example.forzautils.ui.theme.ForzaUtilsTheme
import com.example.forzautils.viewModels.forzaViewModel.ForzaViewModel
import com.example.forzautils.viewModels.forzaViewModel.ForzaViewModelFactory
import com.example.forzautils.viewModels.networkInfo.NetworkInfoViewModel
import com.example.forzautils.viewModels.networkInfo.NetworkInfoViewModelFactory
import com.example.forzautils.viewModels.themeViewModel.ThemeViewModel
import com.example.forzautils.viewModels.themeViewModel.ThemeViewModelFactory
import java.net.SocketException

class MainActivity : ComponentActivity() {

  private val _tag: String = "MainActivity"

  private lateinit var wiFiService: WiFiService
  private lateinit var forzaService: ForzaService
  private val themeViewModel by viewModels<ThemeViewModel> {
    ThemeViewModelFactory(false)
  }
  private val networkInfoViewModel by viewModels<NetworkInfoViewModel> {
    NetworkInfoViewModelFactory(wiFiService, forzaService)
  }
  private val forzaViewModel by viewModels<ForzaViewModel> {
    ForzaViewModelFactory(forzaService)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    wiFiService = WiFiService(this)
    forzaService = ForzaService(this, object : ForzaServiceCallbacks {
      override fun onSocketException(e: SocketException) {
        Log.e(_tag, "Socket exception: ${e.message}")
      }
    })
    val uiModeManager = getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
    val isDarkMode = uiModeManager.nightMode == UiModeManager.MODE_NIGHT_YES
    themeViewModel.setTheme(isDarkMode)
    enableEdgeToEdge()
    wiFiService.inetState.observeForever(wifiObserver)
    setContent {
      ForzaUtilsTheme(themeViewModel) {
        ForzaApp(
          themeViewModel,
          networkInfoViewModel,
          forzaViewModel
        )
      }
    }
  }

  override fun onDestroy() {
    super.onDestroy()
    Log.d(_tag, "Destroying")
    wiFiService.stop()
    forzaService.stop()
  }

  private val wifiObserver =
    Observer<WiFiService.InetState?> { value ->
      if (value == null
        && forzaService.forzaListening.value != null
      ) {
        forzaService.stop()
      }
      if (value != null
        && forzaService.forzaListening.value == null
      ) {
        forzaService.start()
      }
    }
}