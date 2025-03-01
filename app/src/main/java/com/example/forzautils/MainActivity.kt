package com.example.forzautils

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.ui.platform.ComposeView
import com.example.forzautils.services.ForzaService
import com.example.forzautils.services.WiFiService
import com.example.forzautils.ui.AppContainer
import com.example.forzautils.ui.ForzaNavigation
import com.example.forzautils.ui.dataViewer.DataViewerViewModel
import com.example.forzautils.ui.theme.ForzaUtilsTheme
import com.example.forzautils.utils.Constants
import com.example.forzautils.utils.OffloadThread
import com.example.forzautils.viewModels.NetworkInfoViewModel
import com.example.forzautils.viewModels.NetworkInfoViewModelFactory

class MainActivity : ComponentActivity() {

    private val _tag: String = "MainActivity"

    private val _networkInfoViewModel: NetworkInfoViewModel by viewModels {
        NetworkInfoViewModelFactory(wiFiService)
    }
    private val _dataViewerViewModel: DataViewerViewModel by viewModels()

    private lateinit var wiFiService: WiFiService
    private lateinit var forzaService: ForzaService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        initializeServices()
        initializeViewModels()
        setContent {
            ComposeView(this).apply {
                ForzaUtilsTheme {
                    AppContainer{
//                        SplashPage()
//                        LandingPage(_networkInfoViewModel)
                        ForzaNavigation(networkInfoViewModel = _networkInfoViewModel)
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        OffloadThread.Instance().post {
            wiFiService.checkNetwork()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        wiFiService.stop()
        forzaService.stop()
        OffloadThread.Instance().interrupt()
    }

    private fun initializeServices() {
        wiFiService = WiFiService(
            Constants.Inet.PORT,
            baseContext
        )
        forzaService = ForzaService(wiFiService.port, applicationContext)
    }

    private fun initializeViewModels() {
        _dataViewerViewModel.forzaService = forzaService
    }
}