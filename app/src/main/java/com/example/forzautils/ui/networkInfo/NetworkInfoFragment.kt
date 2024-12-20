package com.example.forzautils.ui.networkInfo

import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.forzautils.R

class NetworkInfoFragment(private val viewModel: NetworkInfoViewModel) : Fragment() {

    private val _tag = "HomeFragment"
    private lateinit var view: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(_tag, "onCreateView()")
        view = inflater.inflate(R.layout.fragment_network_info, container, false)
        view.findViewById<Button>(R.id.home_btn_ready)
            .setOnClickListener {
                viewModel.onReadyClicked()
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
            resources.getString(R.string.networkInfo_wifi_error)
        view.findViewById<TextView>(R.id.telemetry_note).visibility = View.INVISIBLE
    }

    private fun showConnectionInfo(ip: String, port: Int) {
        val ipString = String.format(resources.getString(R.string.networkInfo_ip_label), ip)
        val portString = String.format(resources.getString(R.string.networkInfo_port_label), port)
        view.findViewById<TextView>(R.id.connection_label).text =
            resources.getString(R.string.networkInfo_connection_text)
        view.findViewById<TextView>(R.id.ip_textView).text = ipString
        view.findViewById<TextView>(R.id.port_textView).text = portString
        view.findViewById<TextView>(R.id.telemetry_note).visibility = View.VISIBLE
    }
}