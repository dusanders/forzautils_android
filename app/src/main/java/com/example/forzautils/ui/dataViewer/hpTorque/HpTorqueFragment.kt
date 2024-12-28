package com.example.forzautils.ui.dataViewer.hpTorque

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.OnClickListener
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.example.forzautils.R
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineDataSet
import com.google.android.material.button.MaterialButton

class HpTorqueFragment : Fragment() {
    private val _tag = "HpTorqueFragment"
    private var isShowingCurves: Boolean = false
    private val viewModel: HpTorqueViewModel by activityViewModels()
    private lateinit var view: View
    private lateinit var chartFactory: ChartFactory

    private val forzaObserver = Observer<HpTorqueViewModel.DataUpdate> { updated ->
        runOnUiThread {
            redrawGraphForGear(updated.gear)
        }
    }

    private val backButtonListener = OnClickListener { _ ->
        viewModel.onBackClick()
    }

    private val clearButtonListener = OnClickListener { _ ->
        viewModel.clear()
        showWaitingForData()
        clearCharts()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        view = inflater.inflate(R.layout.fragment_hp_torque, container, false)
        chartFactory = ChartFactory(requireContext())
        showWaitingForData()
        return view
    }

    override fun onResume() {
        super.onResume()

        if (viewModel.dataMap.isEmpty()) {
            showWaitingForData()
        } else {
            showCurves()
            rebuildAllGraphs()
        }
        attachObservers()
        // TODO - Remove debug values
//        Handler(Looper.myLooper()!!).postDelayed({
//            viewModel.ADD_DEBUG()
//        }, 3000)
    }

    override fun onStop() {
        super.onStop()
        removeObservers()
        clearCharts()
    }

    private fun clearCharts() {
        runOnUiThread {
            view.findViewById<LinearLayout>(R.id.hpTorque_curvesRoot)
                .removeAllViews()
            chartFactory.clear()
        }
    }

    private fun redrawGraphForGear(gear: Int) {
        val rpmMap = viewModel.dataMap[gear]
        if (rpmMap == null) {
            Log.w(_tag, "ViewModel missing data for $gear")
            return
        }

        val chart = chartFactory.getChartForGear(gear)
        Log.d(_tag, "Chart for $gear")
        val hpData = chart.chart.data.dataSets[0] as LineDataSet
        val tqData = chart.chart.data.dataSets[1] as LineDataSet

        val sortedRpms = rpmMap.keys.sorted()

        hpData.values = sortedRpms.map { key ->
            Entry(key.toFloat(), rpmMap[key]!!.hp)
        }
        tqData.values = sortedRpms.map { key ->
            Entry(key.toFloat(), rpmMap[key]!!.tq)
        }
        invalidateChart(chart)
    }

    private fun invalidateChart(chart: ChartFactory.HpTqChart) {
        if (!isShowingCurves) {
            showCurves()
        }
        maybeAddChart(chart)
        chart.chart.invalidate()
        chart.chart.data.notifyDataChanged()
        chart.chart.notifyDataSetChanged()
    }

    private fun rebuildAllGraphs() {
        val sortedGearKeys = viewModel.dataMap.keys.sorted()
        sortedGearKeys.forEach { gear ->
            redrawGraphForGear(gear)
        }
    }

    private fun runOnUiThread(runnable: Runnable) {
        activity?.runOnUiThread(runnable)
    }

    private fun showWaitingForData() {
        isShowingCurves = false
        view.findViewById<LinearLayout>(R.id.hpTorque_waitForDataLayout)
            .visibility = VISIBLE
        view.findViewById<ScrollView>(R.id.hpTorque_scrollView)
            .visibility = GONE
    }

    private fun showCurves() {
        isShowingCurves = true
        view.findViewById<LinearLayout>(R.id.hpTorque_waitForDataLayout)
            .visibility = GONE
        view.findViewById<ScrollView>(R.id.hpTorque_scrollView)
            .visibility = VISIBLE
    }

    private fun maybeAddChart(chart: ChartFactory.HpTqChart) {
        fun doAddView() {
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                resources.getDimension(R.dimen.hpTorque_lineChartHeight).toInt()
            )
            params.setMargins(0, 20, 0, 20)
            view.findViewById<LinearLayout>(R.id.hpTorque_curvesRoot)
                .addView(chart.chart, params)
        }
        view.findViewById<LinearLayout>(R.id.hpTorque_curvesRoot)
            .findViewById<LineChart>(chart.id)
            ?: doAddView()
    }

    private fun attachObservers() {
        viewModel.dataUpdated.observe(this, forzaObserver)
        view.findViewById<MaterialButton>(R.id.hpTorque_backButton)
            .setOnClickListener(backButtonListener)
        view.findViewById<MaterialButton>(R.id.hpTorque_clearBtn)
            .setOnClickListener(clearButtonListener)
    }

    private fun removeObservers() {
        viewModel.dataUpdated.removeObserver(forzaObserver)
    }
}