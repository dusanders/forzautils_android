package com.example.forzautils

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.ui.platform.ComposeView
import com.example.forzautils.services.ForzaService
import com.example.forzautils.services.WiFiService
import com.example.forzautils.ui.AppContainer
import com.example.forzautils.ui.ForzaApp
import com.example.forzautils.ui.dataViewer.DataViewerViewModel
import com.example.forzautils.ui.theme.ForzaUtilsTheme
import com.example.forzautils.utils.Constants
import com.example.forzautils.utils.OffloadThread

class MainActivity : ComponentActivity() {

  private val _tag: String = "MainActivity"

  private val _dataViewerViewModel: DataViewerViewModel by viewModels()

  private var wiFiService: WiFiService? = null
  private var forzaService: ForzaService? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    initializeServices()
    initializeViewModels()
    setContent {
      ComposeView(this).apply {
        ForzaUtilsTheme {
          AppContainer {
            ForzaApp(wiFiService!!, forzaService!!)
          }
        }
      }
    }
  }

  override fun onResume() {
    super.onResume()
    initializeServices()
  }

  override fun onDestroy() {
    super.onDestroy()
    wiFiService?.stop()
    forzaService?.stop()
    OffloadThread.Instance().interrupt()
    wiFiService = null;
    forzaService = null;
  }

  private fun initializeServices() {
    fun initWifiService() {
      if (wiFiService == null) {
        wiFiService = WiFiService(
          Constants.Inet.PORT,
          baseContext
        )
        wiFiService?.start()
      }
    }
    fun initForzaService() {
      if (forzaService == null) {
        forzaService = ForzaService(
          wiFiService!!,
          applicationContext
        )
      }
    }
    initWifiService()
    initForzaService()
  }

  private fun initializeViewModels() {
    _dataViewerViewModel.forzaService = forzaService!!
  }
}