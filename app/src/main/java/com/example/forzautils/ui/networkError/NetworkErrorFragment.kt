package com.example.forzautils.ui.networkError

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.forzautils.R

class NetworkErrorFragment() : Fragment() {

    private lateinit var view: View
    private val viewModel: NetworkErrorViewModel by activityViewModels()

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