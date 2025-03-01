package com.example.forzautils

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.forzautils.services.ForzaService
import com.example.forzautils.services.WiFiService
import com.example.forzautils.ui.AppContainer
import com.example.forzautils.ui.ForzaNavigation
import com.example.forzautils.ui.dataViewer.DataViewerFragment
import com.example.forzautils.ui.dataViewer.DataViewerViewModel
import com.example.forzautils.ui.networkError.NetworkErrorFragment
import com.example.forzautils.ui.networkError.NetworkErrorViewModel
import com.example.forzautils.ui.networkInfo.NetworkInfoFragment
import com.example.forzautils.viewModels.NetworkInfoViewModel
import com.example.forzautils.ui.splash.SplashFragment
import com.example.forzautils.ui.splash.SplashViewModel
import com.example.forzautils.ui.theme.ForzaUtilsTheme
import com.example.forzautils.utils.Constants
import com.example.forzautils.utils.OffloadThread
import com.example.forzautils.viewModels.NetworkInfoViewModelFactory

class MainActivity : ComponentActivity(), NetworkErrorViewModel.Callback {
    enum class Pages {
        SPLASH,
        DATA_VIEWER,
        NETWORK_INFO,
        NETWORK_ERROR
    }

    private val _tag: String = "MainActivity"

    private val _networkInfoViewModel: NetworkInfoViewModel by viewModels {
        NetworkInfoViewModelFactory(wiFiService)
    }
    private val _splashViewModel: SplashViewModel by viewModels()
    private val _networkErrorViewModel: NetworkErrorViewModel by viewModels()
    private val _dataViewerViewModel: DataViewerViewModel by viewModels()

    private val currentFragment: MutableLiveData<Pages> = MutableLiveData(Pages.SPLASH)
    private lateinit var wiFiService: WiFiService
    private lateinit var forzaService: ForzaService

    private val forzaListeningObserver: Observer<Boolean> = Observer { listening ->
        Log.d(_tag, "ForzaListener now listening... $listening")
        if (listening) {
            wiFiService.inetState.value?.let { _networkInfoViewModel.setInetState(it) }
            if (currentFragment.value == Pages.SPLASH) {
                currentFragment.postValue(Pages.NETWORK_INFO)
            }
        }
    }

    private val wifiInetObserver: Observer<WiFiService.InetState> = Observer { inet ->
        if (inet.ipString == Constants.Inet.DEFAULT_IP) {
            currentFragment.postValue(Pages.NETWORK_ERROR)
        } else {
            forzaService.start(wiFiService)
        }
    }

    private val forzaConnectedObserver: Observer<Boolean> = Observer {
        Log.d(_tag, "Forza connected")
    }

    private val homeReadyBtnObserver: Observer<Boolean> = Observer {
        Log.d(_tag, "Home ready clicked")
        currentFragment.postValue(Pages.DATA_VIEWER)
    }

    private val fragmentObserver: Observer<Pages> = Observer { page ->
        updateFragment(page)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        initializeServices()
        initializeViewModels()
        attachObservers()
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

//        setContentView(R.layout.activity_main)
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }
    }

    override fun onResume() {
        super.onResume()
        OffloadThread.Instance().post {
            wiFiService.checkNetwork()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        removeObservers()
        wiFiService.stop()
        forzaService.stop()
        OffloadThread.Instance().interrupt()
    }


    override fun onRetryNetworkClicked() {
        Log.d(_tag, "Network error - retry clicked")
        wiFiService.checkNetwork()
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
        _networkErrorViewModel.setCallback(this)
    }

    private fun removeObservers() {
        wiFiService.inetState.removeObserver(wifiInetObserver)
        forzaService.forzaListening.removeObserver(forzaListeningObserver)
        forzaService.forzaConnected.removeObserver(forzaConnectedObserver)
        currentFragment.removeObserver(fragmentObserver)
    }

    private fun attachObservers() {
        wiFiService.inetState.observe(this, wifiInetObserver)
        forzaService.forzaListening.observe(this, forzaListeningObserver)
        forzaService.forzaConnected.observe(this, forzaConnectedObserver)
        currentFragment.observe(this, fragmentObserver)
    }

    private fun updateFragment(page: Pages) {
        var fragment: Fragment = SplashFragment(_splashViewModel)
        when (page) {
            Pages.SPLASH -> {
                // no-op - we already set the fragment as the splash fragment
            }

            Pages.NETWORK_INFO -> {
                fragment = NetworkInfoFragment()
            }

            Pages.NETWORK_ERROR -> {
                fragment = NetworkErrorFragment()
            }

            Pages.DATA_VIEWER -> {
                fragment = DataViewerFragment()
            }
        }
//        supportFragmentManager.beginTransaction()
//            .replace(R.id.mainFragment_contentView, fragment)
//            .commit()
    }
}