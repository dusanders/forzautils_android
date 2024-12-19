package com.example.forzautils.ui.networkError

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.forzautils.R

class NetworkErrorFragment(private val viewModel: NetworkErrorViewModel) : Fragment() {

    private lateinit var view: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        view = inflater.inflate(R.layout.fragment_network_error, container, false)
        view.findViewById<Button>(R.id.networkError_retryBtn)
            .setOnClickListener {
                viewModel.onRetryClicked()
            }
        return view
    }
}