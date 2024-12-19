package com.example.forzautils.ui.home

import android.content.Context
import android.graphics.Typeface
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.Observer
import com.example.forzautils.R
import com.example.forzautils.utils.Constants
import java.net.NetworkInterface

class HomeFragment(private val viewModel: HomeViewModel) : Fragment() {

    private val _tag = "HomeFragment"
    private lateinit var view: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(_tag, "onCreateView()")
        view = inflater.inflate(R.layout.fragment_home, container, false)
        view.findViewById<Button>(R.id.btn_forzaVersion2023)
            .setOnClickListener {
                viewModel.setForzaVersion(Constants.ForzaVersion.FORZA_2023)
            }
        view.findViewById<Button>(R.id.btn_forzaVersion7)
            .setOnClickListener {
                viewModel.setForzaVersion(Constants.ForzaVersion.FORZA_7)
            }
        view.findViewById<TextView>(R.id.telemetry_note).typeface =
            Typeface.create(null, 700, false)
        return view
    }

    override fun onResume() {
        super.onResume()
        Log.d(_tag, "onResume()")
        viewModel.inetError.observe(this) { isError ->
            isError ?: showIpError()
        }
        viewModel.inetInfo.observe(this) { info ->
            Log.d(_tag, "inet update: ${info.ip}")
            showConnectionInfo(info.ip, info.port)
        }
    }

    private fun showIpError() {
        view.findViewById<TextView>(R.id.ip_textView).text = ""
        view.findViewById<TextView>(R.id.port_textView).text = ""
        view.findViewById<TextView>(R.id.connection_label).text =
            resources.getString(R.string.home_wifi_error)
        view.findViewById<TextView>(R.id.telemetry_note).visibility = View.INVISIBLE
    }

    private fun showConnectionInfo(ip: String, port: Int) {
        val ipString = String.format(resources.getString(R.string.home_ip_label), ip)
        val portString = String.format(resources.getString(R.string.home_port_label), port)
        view.findViewById<TextView>(R.id.connection_label).text =
            resources.getString(R.string.home_connection_text)
        view.findViewById<TextView>(R.id.ip_textView).text = ipString
        view.findViewById<TextView>(R.id.port_textView).text = portString
        view.findViewById<TextView>(R.id.telemetry_note).visibility = View.VISIBLE
    }
}