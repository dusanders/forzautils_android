package com.example.forzautils.ui.splash

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.lifecycle.Observer
import com.example.forzautils.R

class SplashFragment(private val viewModel: SplashViewModel) : Fragment() {

    private val _tag: String = "SplashFragment"
    private lateinit var _view: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.loadingState.observe(this, Observer { loadingState ->
            Log.d(_tag, "change state to ${loadingState}")
            when(loadingState) {
                SplashViewModel.LoadingState.LOADING ->
                    _view.findViewById<TextView>(R.id.splash_textView)
                        .setText(R.string.splash_appName)
                SplashViewModel.LoadingState.FINISHED ->
                    _view.findViewById<ProgressBar>(R.id.splash_progressBar)
                        .visibility = View.GONE
                null -> {
                    // no-op
                }
            }
        })
        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_splash, container, false)
        _view = view
        return view
    }
}