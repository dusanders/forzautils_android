package com.example.forzautils

import android.app.UiModeManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.ui.platform.ComposeView
import com.example.forzautils.services.ForzaService
import com.example.forzautils.services.ForzaServiceCallbacks
import com.example.forzautils.services.WiFiService
import com.example.forzautils.ui.ForzaApp
import com.example.forzautils.ui.dataViewer.DataViewerViewModel
import com.example.forzautils.ui.pages.splash.SplashPage
import com.example.forzautils.ui.theme.ForzaUtilsTheme
import com.example.forzautils.utils.Constants
import com.example.forzautils.utils.observeUntil
import com.example.forzautils.viewModels.themeViewModel.ThemeViewModel
import com.example.forzautils.viewModels.themeViewModel.ThemeViewModelFactory
import java.net.SocketException

class MainActivity : ComponentActivity() {

  private val _tag: String = "MainActivity"

  private val _dataViewerViewModel: DataViewerViewModel by viewModels()
  private val themeViewModel by viewModels<ThemeViewModel> {
    ThemeViewModelFactory(false)
  }

  private lateinit var wiFiService: WiFiService
  private lateinit var forzaService: ForzaService

  @RequiresApi(Build.VERSION_CODES.S)
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val uiModeManager = getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
    val isDarkMode = uiModeManager.nightMode == UiModeManager.MODE_NIGHT_YES
    themeViewModel.setTheme(isDarkMode)
    enableEdgeToEdge()
    start()
  }

  @RequiresApi(Build.VERSION_CODES.S)
  override fun onDestroy() {
    super.onDestroy()
    wiFiService.stop()
    forzaService.stop()
  }

  @RequiresApi(Build.VERSION_CODES.S)
  private fun start() {
    wiFiService = WiFiService(
      Constants.Inet.PORT,
      baseContext
    )

    setContent {
      ComposeView(this).apply {
        ForzaUtilsTheme(themeViewModel) {
          SplashPage()
        }
      }
    }
    wiFiService.inetState.observeUntil(
      this,
      { it != null },
      {
        if (it != null) {
          forzaService = ForzaService(
            wiFiService,
            baseContext,
            forzaServiceCallback
          )
          setContent {
            ForzaUtilsTheme(themeViewModel) {
              ForzaApp(
                themeViewModel,
                wiFiService,
                forzaService
              )
            }
          }
        }
      }
    )
  }

  @RequiresApi(Build.VERSION_CODES.S)
  private fun restart() {
    wiFiService.stop()
  }

  private val forzaServiceCallback = object : ForzaServiceCallbacks {
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onSocketException(e: SocketException) {
      Log.w(_tag, "SocketException: ${e.message}")
      restart()
    }
  }
}