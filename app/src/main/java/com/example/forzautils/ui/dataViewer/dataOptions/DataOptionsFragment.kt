package com.example.forzautils.ui.dataViewer.dataOptions

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.forzautils.R

class DataOptionsFragment(private val viewModel: DataOptionsViewModel) : Fragment() {

    private val _tag = "DataOptionsFragment"
    private lateinit var view: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        view = inflater.inflate(R.layout.fragment_data_options, container, false)
        attachClickListeners()
        return view
    }

    private fun attachClickListeners() {
        view.findViewById<Button>(R.id.data_options_dynoBtn).setOnClickListener {
            Log.d(_tag, "HP/Torque button click")
            viewModel.userClick_hpTorque()
        }
    }
}