package com.example.forzautils

import android.app.UiModeManager
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.ui.platform.ComposeView
import com.example.forzautils.services.ForzaService
import com.example.forzautils.services.WiFiService
import com.example.forzautils.ui.ForzaApp
import com.example.forzautils.ui.dataViewer.DataViewerViewModel
import com.example.forzautils.ui.pages.splash.SplashPage
import com.example.forzautils.ui.theme.ForzaUtilsTheme
import com.example.forzautils.utils.Constants
import com.example.forzautils.utils.observeUntil
import com.example.forzautils.viewModels.themeViewModel.ThemeViewModel
import com.example.forzautils.viewModels.themeViewModel.ThemeViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class MainActivity : ComponentActivity() {

  private val _tag: String = "MainActivity"

  private val _dataViewerViewModel: DataViewerViewModel by viewModels()
  val themeViewModel by viewModels<ThemeViewModel> {
    ThemeViewModelFactory(false)
  }

  private var wiFiService: WiFiService? = null
  private var forzaService: ForzaService? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val uiModeManager = getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
    val isDarkMode = uiModeManager.nightMode == UiModeManager.MODE_NIGHT_YES
    themeViewModel.setTheme(isDarkMode)
    enableEdgeToEdge()
    showSplash()
    offloadInit()
  }

  override fun onDestroy() {
    super.onDestroy()
    wiFiService?.stop()
    forzaService?.stop()
    wiFiService = null;
    forzaService = null;
  }

  private fun showSplash() {
    setContent {
      ComposeView(this).apply {
        ForzaUtilsTheme(themeViewModel) {
          SplashPage()
        }
      }
    }
  }

  private fun offloadInit() {
    CoroutineScope(Dispatchers.IO).launch {
      val forzaService = async { initializeServices() }.await()
      initializeViewModels(forzaService)

      withContext(Dispatchers.Main) {
        setContent {
          ComposeView(baseContext).apply {
            ForzaUtilsTheme(themeViewModel) {
              ForzaApp(
                themeViewModel,
                wiFiService!!,
                forzaService
              )
            }
          }
        }
      }
    }
  }

  private suspend fun initializeWifiServer(): WiFiService =
    suspendCoroutine { continuation ->
      if (wiFiService == null) {
        Log.d(_tag, "Initializing WiFi server...")
        wiFiService = WiFiService(
          Constants.Inet.PORT,
          baseContext
        )
        Handler(mainLooper).post {
          Log.d(_tag, "Initializing Wifi Service - UI Thread")
          wiFiService?.inetState?.observeUntil(this,
            { it != null },
            {
              if (it != null) {
                Log.d(_tag, "Initializing Wifi Service CALLBACK - $it")
                continuation.resume(wiFiService!!)
              }
            })
        }
      } else {
        Log.d(_tag, "Already have wifi service - return from coroutine")
        continuation.resume(wiFiService!!)
      }
    }

  private suspend fun initializeServices(): ForzaService =
    suspendCoroutine { continuation ->
      Log.d(_tag, "Initializing services...")
      CoroutineScope(Dispatchers.IO).launch {
        Log.d(_tag, "Initializing services... in coroutine")
        val wifiService = async { initializeWifiServer() }.await()
        forzaService = ForzaService(
          wifiService,
          baseContext
        )
        continuation.resume(forzaService!!)
      }
    }

  private fun initializeViewModels(forzaService: ForzaService) {
    _dataViewerViewModel.forzaService = forzaService
  }
}