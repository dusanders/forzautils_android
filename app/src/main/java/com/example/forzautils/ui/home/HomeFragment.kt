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
import java.net.NetworkInterface

class HomeFragment : Fragment() {

    companion object {
        fun newInstance() = HomeFragment()
    }

    private val _tag = "HomeFragment"
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var view: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(_tag, "onCreateView()")
        view = inflater.inflate(R.layout.fragment_home, container, false)
        view.findViewById<Button>(R.id.btn_forzaVersion2023)
            .setOnClickListener { view ->
                viewModel.setForzaVersion(HomeViewModel.ForzaVersion.FM_2023)
            }
        view.findViewById<Button>(R.id.btn_forzaVersion7)
            .setOnClickListener { view ->
                viewModel.setForzaVersion(HomeViewModel.ForzaVersion.FM_7)
            }
        view.findViewById<TextView>(R.id.telemetry_note).typeface = Typeface.create(null, 700, false)
        return view
    }

    override fun onResume() {
        super.onResume()
        Log.d(_tag, "onResume()")
        viewModel.inetState.observe(this, { inetInfo ->
            Log.d(_tag, "Ip updated: ${inetInfo.ipString}")
            if (inetInfo.ipString.equals(HomeViewModel.LOOPBACK_IP)) {
                showIpError()
            } else {
                showConnectionInfo(inetInfo.ipString, inetInfo.port)
            }
        })
        viewModel.updateIpInfo()
    }

    fun showIpError() {
        view.findViewById<TextView>(R.id.ip_textView).text = ""
        view.findViewById<TextView>(R.id.port_textView).text = ""
        view.findViewById<TextView>(R.id.connection_label).text =
            resources.getString(R.string.home_wifi_error)
        view.findViewById<TextView>(R.id.telemetry_note).visibility = View.INVISIBLE
    }

    fun showConnectionInfo(ip: String, port: Int) {
        val ipString = String.format(resources.getString(R.string.home_ip_label), ip)
        val portString = String.format(resources.getString(R.string.home_port_label), port)
        view.findViewById<TextView>(R.id.connection_label).text =
            resources.getString(R.string.home_connection_text)
        view.findViewById<TextView>(R.id.ip_textView).text = ipString
        view.findViewById<TextView>(R.id.port_textView).text = portString
        view.findViewById<TextView>(R.id.telemetry_note).visibility = View.VISIBLE
    }
}