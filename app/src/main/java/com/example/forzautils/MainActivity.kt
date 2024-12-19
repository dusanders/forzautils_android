package com.example.forzautils

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.forzautils.services.ForzaService
import com.example.forzautils.services.WiFiService
import com.example.forzautils.ui.home.HomeFragment
import com.example.forzautils.ui.home.HomeViewModel
import com.example.forzautils.ui.networkError.NetworkErrorFragment
import com.example.forzautils.ui.networkError.NetworkErrorViewModel
import com.example.forzautils.ui.networkError.NetworkErrorViewModelFactory
import com.example.forzautils.ui.splash.SplashFragment
import com.example.forzautils.ui.splash.SplashViewModel
import com.example.forzautils.utils.Constants
import com.example.forzautils.utils.OffloadThread
import java.util.Timer
import kotlin.concurrent.schedule

class MainActivity : AppCompatActivity(), NetworkErrorViewModel.Callback {
    enum class Pages {
        SPLASH,
        HOME,
        NETWORK_ERROR
    }

    private val _tag: String = "MainActivity"

    private val _homeViewModel: HomeViewModel by viewModels()
    private val _splashViewModel: SplashViewModel by viewModels()
    private val _networkErrorViewModel: NetworkErrorViewModel by viewModels {
        NetworkErrorViewModelFactory(this)
    }

    private lateinit var wiFiService: WiFiService
    private lateinit var forzaService: ForzaService

    private val forzaListeningObserver: Observer<Boolean> = Observer { listening ->
        Log.d(_tag, "ForzaListener now listening... $listening")
        if (listening) {
            wiFiService.inetState.value?.let { _homeViewModel.setInetState(it) }
            navigate(Pages.HOME)
        }
    }

    private val wifiInetObserver: Observer<WiFiService.InetState> = Observer { inet ->
        if (inet.ipString == Constants.DEFAULT_IP) {
            navigate(Pages.NETWORK_ERROR)
        } else {
            forzaService.start()
        }
    }

    private val forzaConnectedObserver: Observer<Boolean> = Observer { connected ->
        Log.d(_tag, "Forza connected")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        initializeServices()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun onResume() {
        super.onResume()
        navigate(Pages.SPLASH)
        Timer(_tag, false).schedule(2000) {
            runOnUiThread {
                attachObservers()
            }
            wiFiService.checkNetwork()
        }
    }

    override fun onStop() {
        super.onStop()
        removeObservers()
        wiFiService.stop()
        forzaService.stop()
        OffloadThread.Instance().interrupt()
    }

    override fun onRetryNetworkClicked() {
        Log.d(_tag, "Network error - retry clicked")
    }

    private fun initializeServices() {
        wiFiService = WiFiService(
            Constants.PORT,
            baseContext
        )
        forzaService = ForzaService(wiFiService.port)
    }

    private fun removeObservers() {
        wiFiService.inetState.removeObserver(wifiInetObserver)
        forzaService.forzaListening.removeObserver(forzaListeningObserver)
        forzaService.forzaConnected.removeObserver(forzaConnectedObserver)
    }

    private fun attachObservers() {
        wiFiService.inetState.observe(this, wifiInetObserver)
        forzaService.forzaListening.observe(this, forzaListeningObserver)
        forzaService.forzaConnected.observe(this, forzaConnectedObserver)
    }

    private fun navigate(page: Pages) {
        var fragment: Fragment = SplashFragment(_splashViewModel)
        when (page) {
            Pages.SPLASH -> {
                // no-op - we alread set the fragment as the splash fragment
            }

            Pages.HOME -> {
                fragment = HomeFragment(_homeViewModel)
            }

            Pages.NETWORK_ERROR -> {
                fragment = NetworkErrorFragment(_networkErrorViewModel)
            }
        }
        supportFragmentManager.beginTransaction()
            .replace(R.id.mainFragment_contentView, fragment)
            .commit()
    }
}