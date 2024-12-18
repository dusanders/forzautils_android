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
import com.example.forzautils.utils.OffloadThread
import forza.telemetry.ForzaInterface
import forza.telemetry.ForzaTelemetryApi
import forza.telemetry.ForzaTelemetryBuilder
import forza.telemetry.VehicleData
import java.net.DatagramPacket
import java.util.Timer
import kotlin.concurrent.schedule

class MainActivity : AppCompatActivity(), ForzaInterface {
    enum class Pages {
        SPLASH,
        HOME
    }

    private val _homeViewModel : HomeViewModel by viewModels()
    private val _splashViewModel: SplashViewModel by viewModels()

    private val _tag: String = "MainActivity"
    private var telemetryBuilder: ForzaTelemetryBuilder? = null
    private var telemetryBuilderThread: Thread? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
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
        ensureTelemetry()
        _homeViewModel.inetState.observe(this, {inet ->
            Log.d(_tag, "HomeViewModel got new inet ${inet.ipString} @ ${inet.port}")
            _splashViewModel.setLoadingState(SplashViewModel.LoadingState.FINISHED)
            navigate(Pages.HOME)
        })
        Timer(_tag, false).schedule(2000) {
            _homeViewModel.updateIpInfo()
        }
    }

    override fun onStop() {
        super.onStop()
        if(telemetryBuilderThread != null && telemetryBuilderThread!!.isAlive) {
            Log.w(_tag, "onStop() - Stopping telemetry thread")
            telemetryBuilderThread?.interrupt()
        }
    }

    private fun ensureTelemetry() {
        if(telemetryBuilder == null) {
            telemetryBuilder = _homeViewModel.inetState.value?.port?.let { ForzaTelemetryBuilder(it) }
            telemetryBuilder?.addListener(this)
        }
        if(telemetryBuilderThread == null) {
            Log.w(_tag, "TelemetryBuilder Thread is null - starting new...")
            telemetryBuilderThread = telemetryBuilder?.thread
            OffloadThread.Instance()
                .post({
                    telemetryBuilderThread?.start()
                })
        } else if(!telemetryBuilderThread!!.isAlive) {
            Log.w(_tag, "TelemetryBuilder thread is DEAD - starting...")
            OffloadThread.Instance()
                .post({
                    telemetryBuilderThread?.start()
                })
        }
    }

    private fun navigate(page: Pages) {
        val transaction = supportFragmentManager.beginTransaction()
        when(page) {
            Pages.SPLASH -> {
                transaction.replace(R.id.mainFragment_contentView, SplashFragment())
                    .commit()
            }
            Pages.HOME -> {
                transaction.replace(R.id.mainFragment_contentView, HomeFragment())
                    .commit()
                runOnUiThread({
                    _homeViewModel.version.observe(this) { selectedVersion ->
                        Log.d(_tag, "Selected $selectedVersion")
                    }
                })
            }
        }
    }

    override fun onDataReceived(api: ForzaTelemetryApi?) {
        TODO("Not yet implemented")
    }

    override fun onConnected(api: ForzaTelemetryApi?, packet: DatagramPacket?) {
        TODO("Not yet implemented")
    }

    override fun onGamePaused() {
        TODO("Not yet implemented")
    }

    override fun onGameUnpaused() {
        TODO("Not yet implemented")
    }

    override fun onCarChanged(api: ForzaTelemetryApi?, data: VehicleData?) {
        TODO("Not yet implemented")
    }
}