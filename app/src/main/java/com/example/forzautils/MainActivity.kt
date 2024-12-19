package com.example.forzautils

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.forzautils.ui.home.HomeFragment
import com.example.forzautils.ui.home.HomeViewModel
import com.example.forzautils.ui.splash.SplashFragment
import com.example.forzautils.ui.splash.SplashViewModel
import com.example.forzautils.utils.Constants
import com.example.forzautils.utils.ForzaListener
import com.example.forzautils.utils.OffloadThread
import java.util.Timer
import kotlin.concurrent.schedule

class MainActivity : AppCompatActivity() {
    enum class Pages {
        SPLASH,
        HOME
    }

    private val _tag: String = "MainActivity"
    private val _homeViewModel: HomeViewModel by viewModels()
    private val _splashViewModel: SplashViewModel by viewModels()
    private lateinit var _forzaListener: ForzaListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        _forzaListener = ForzaListener(Constants.PORT)
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
        attachForzaListener()
        Timer(_tag, false).schedule(2000) {
            _forzaListener.start()
        }
    }

    override fun onStop() {
        super.onStop()
        _forzaListener.stop()
        OffloadThread.Instance().interrupt()
    }

    private fun attachForzaListener() {
        _forzaListener.forzaListening.observe(this) { listening ->
            Log.d(_tag, "ForzaListener now listening... $listening")
        }
        _forzaListener.inetState.observe(this) { inet ->
            Log.d(_tag, "ForzaListener updated inet ${inet.ipString}")
            _splashViewModel.setLoadingState(SplashViewModel.LoadingState.FINISHED)
            navigate(Pages.HOME)
            _homeViewModel.setInetState(inet)
        }
    }

    private fun navigate(page: Pages) {
        val transaction = supportFragmentManager.beginTransaction()
        when (page) {
            Pages.SPLASH -> {
                transaction.replace(R.id.mainFragment_contentView, SplashFragment(_splashViewModel))
                    .commit()
            }

            Pages.HOME -> {
                transaction.replace(R.id.mainFragment_contentView, HomeFragment(_homeViewModel))
                    .commit()
                runOnUiThread({
                    _homeViewModel.version.observe(this) { selectedVersion ->
                        Log.d(_tag, "Selected $selectedVersion")
                    }
                })
            }
        }
    }
}