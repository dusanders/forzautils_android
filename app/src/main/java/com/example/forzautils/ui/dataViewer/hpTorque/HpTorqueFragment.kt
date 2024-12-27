package com.example.forzautils.ui.dataViewer.hpTorque

import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.example.forzautils.R
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.android.material.button.MaterialButton

class HpTorqueFragment : Fragment() {
    private data class HpTqValueList(
        val hpValues: ArrayList<Entry> = ArrayList(),
        val tqValues: ArrayList<Entry> = ArrayList()
    )

    private val _tag = "HpTorqueFragment"
    private val viewModel: HpTorqueViewModel by activityViewModels()
    private lateinit var view: View

    private var lastGear: Int = 1
    private var lastRpm: Float = 0f
    private var lastHp: Float = 0f
    private var lastTorque: Float = 0f
    private var charts: ArrayList<LineChart> = ArrayList()
    private var hpTqvValues: ArrayList<HpTqValueList> = ArrayList()
    private val forzaDataObserver = Observer<HpTorqueViewModel.HpTqData> { data ->
        if (data.gear < 1 || data.gear > 7) {
            return@Observer
        }
        lastGear = data.gear
        lastRpm = data.rpm
        lastTorque = data.tq
        lastHp = data.hp
        addHpTorque(data)
    }
    private val backButtonListener = OnClickListener { v ->
        viewModel.onBackClick()
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

        // TODO - Remove debug values
//        addHpTorque(HpTorqueViewModel.HpTqData(100f, 123f, 1200f, 1))
//        addHpTorque(HpTorqueViewModel.HpTqData(123f, 140f, 1300f, 1))
//        addHpTorque(HpTorqueViewModel.HpTqData(134f, 150f, 1500f, 1))
//        addHpTorque(HpTorqueViewModel.HpTqData(110f, 109f, 1800f, 1))
//        addHpTorque(HpTorqueViewModel.HpTqData(90f, 54f, 2000f, 1))
//        addHpTorque(HpTorqueViewModel.HpTqData(300f, 210f, 1300f, 2))
//        addHpTorque(HpTorqueViewModel.HpTqData(310f,220f,1400f,2))
//        addHpTorque(HpTorqueViewModel.HpTqData(320f, 320f, 1500f, 2))
//        addHpTorque(HpTorqueViewModel.HpTqData(320f, 350f, 1600f, 2))
    }

    override fun onStop() {
        super.onStop()
        removeObservers()
    }

    private fun getChartForGear(gear: Int): LineChart {
        fun addLineGraphToView(lineGraph: LineChart) {
            view.findViewById<LinearLayout>(R.id.hpTorque_curvesRoot)
                .addView(
                    lineGraph, LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        resources.getDimension(R.dimen.hpTorque_lineChartHeight).toInt()
                    )
                )
        }

        var chart: LineChart
        while (charts.count() < gear) {
            chart = initializeLineChart(
                LineChart(context),
                String.format(
                    resources.getString(R.string.generic_gear),
                    charts.count() + 1
                )
            )
            charts.add(chart)
            addLineGraphToView(chart)
        }
        chart = charts[gear - 1]
        return chart
    }

    private fun addHpTorque(dataEntry: HpTorqueViewModel.HpTqData) {
        while (hpTqvValues.size < dataEntry.gear) {
            hpTqvValues.add(HpTqValueList())
        }
        if(dataEntry.hp < 0) {
            Log.d(_tag, "Ignore 0 horsepower packet")
            return
        }
        val hpTqValueList = hpTqvValues[dataEntry.gear - 1]
        if(hpTqValueList.hpValues.isNotEmpty()) {
            val lastRpmReading = hpTqValueList.hpValues.last().x
            if(lastRpmReading > dataEntry.rpm){
                Log.d(_tag, "Ignore decel in ${dataEntry.gear} @ $lastRpmReading -> ${dataEntry.rpm}")
                return
            }
            val nextRpm = lastRpmReading + 300;
            if(dataEntry.rpm < nextRpm){
                return
            }
        }
        Log.d(_tag, "adding ${dataEntry.gear} @ ${dataEntry.rpm}")
        hpTqValueList.hpValues.add(Entry(dataEntry.rpm, dataEntry.hp))
        hpTqValueList.tqValues.add(Entry(dataEntry.rpm, dataEntry.tq))

        val chart = getChartForGear(dataEntry.gear)
        val hpDataSet = chart.data.dataSets[0] as LineDataSet
        val torqueDataSet = chart.data.dataSets[1] as LineDataSet

        hpDataSet.values = hpTqValueList.hpValues
        torqueDataSet.values = hpTqValueList.tqValues

        chart.invalidate()
        chart.data.notifyDataChanged()
        chart.notifyDataSetChanged()
    }

    private fun initializeLineChart(chart: LineChart, desc: String): LineChart {
        val lineGraph = initializeChartDataSets(chart)
        val textColor = resources.getColor(R.color.hpTorque_text, context?.theme)
        lineGraph.description.textColor = textColor
        lineGraph.xAxis.textColor = textColor
        lineGraph.axisLeft.textColor = textColor
        lineGraph.axisRight.textColor = textColor
        lineGraph.legend.textColor = textColor
        val graphDesc = Description()
        graphDesc.text = desc
        graphDesc.textColor = textColor
        lineGraph.description = graphDesc
        lineGraph.setTouchEnabled(false)
        lineGraph.setPinchZoom(false)
        return lineGraph
    }

    private fun initializeChartDataSets(lineGraph: LineChart): LineChart {
        val hpEntries = ArrayList<Entry>()
        val hpDataSet = LineDataSet(hpEntries, resources.getString(R.string.generic_horsepower))
        hpDataSet.color = resources.getColor(R.color.hpTorque_hpLine, context?.theme)
        hpDataSet.axisDependency = YAxis.AxisDependency.RIGHT

        val tqEntries = ArrayList<Entry>()
        val tqDataSet = LineDataSet(tqEntries, resources.getString(R.string.generic_torque))
        tqDataSet.color = resources.getColor(R.color.hpTorque_torqueLine, context?.theme)

        lineGraph.data = LineData(hpDataSet, tqDataSet)
        return lineGraph
    }

    private fun attachObservers() {
        viewModel.hpTqData.observe(this, forzaDataObserver)
        view.findViewById<MaterialButton>(R.id.hpTorque_backButton)
            .setOnClickListener(backButtonListener)
    }

    private fun removeObservers() {
        viewModel.hpTqData.removeObserver(forzaDataObserver)
    }

    private fun runOnMainUi(runnable: Runnable) {
        if(isAdded) {
            activity?.runOnUiThread(runnable)
                ?: Log.w(_tag, "Failed to run on UI Thread!! Fragment not attached!")
        }
    }
}