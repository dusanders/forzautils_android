package com.example.forzautils.ui.dataViewer.hpTorque

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.example.forzautils.R
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

class HpTorqueFragment : Fragment() {

    private val viewModel: HpTorqueViewModel by activityViewModels()
    private lateinit var view: View;

    private val rpm: Observer<Float> = Observer { rpm ->

    }

    private val hp: Observer<Float> = Observer { hp ->

    }

    private val torque: Observer<Float> = Observer { torque ->

    }

    private val gear: Observer<Int> = Observer { gear ->

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        view = inflater.inflate(R.layout.fragment_hp_torque, container, false)
        return view
    }

    override fun onResume() {
        super.onResume()
        attachObservers()
        view.findViewById<LinearLayout>(R.id.hpTorque_waitForDataLayout)
            .visibility = View.INVISIBLE
        view.findViewById<LinearLayout>(R.id.hpTorque_curvesRoot)
            .visibility = View.VISIBLE
        addLineGraph(view.findViewById(R.id.hpTorque_curvesRoot))
    }

    override fun onStop() {
        super.onStop()
        removeObservers()
    }

    private fun addLineGraph(view: LinearLayout) {
        val lineGraph = LineChart(context)
        lineGraph.layoutParams = ViewGroup.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        val desc = Description()
        desc.text = "Test Description"
        lineGraph.description = desc

        val values = ArrayList<Entry>()
        values.add(Entry(0f,0f))
        values.add(Entry(1f,1f))
        values.add(Entry(2f,2f))
        values.add(Entry(3f,3f))

        val lineDataSet = LineDataSet(values, "Test Data Set")

        val dataSets = LineData(lineDataSet)

        lineGraph.data = dataSets

        view.addView(lineGraph)
    }

    private fun attachObservers() {
        viewModel.torque.observe(this, torque)
        viewModel.hp.observe(this, hp)
        viewModel.gear.observe(this, gear)
        viewModel.rpm.observe(this, rpm)
    }

    private fun removeObservers() {
        viewModel.torque.removeObserver(torque)
        viewModel.hp.removeObserver(hp)
        viewModel.gear.removeObserver(gear)
        viewModel.rpm.removeObserver(rpm)
    }
}