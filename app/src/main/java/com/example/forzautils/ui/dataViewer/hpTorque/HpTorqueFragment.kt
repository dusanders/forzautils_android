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
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.android.material.button.MaterialButton

class HpTorqueFragment : Fragment() {
    private data class HpTqSet(
        val hp: Float = 0f,
        val tq: Float = 0f
    )

    private data class HpTqValueList(
        val hpValues: ArrayList<Entry> = ArrayList(),
        val tqValues: ArrayList<Entry> = ArrayList()
    )

    private val _tag = "HpTorqueFragment"
    private var isShowingCurves: Boolean = false
    private val viewModel: HpTorqueViewModel by activityViewModels()
    private lateinit var view: View

    private var lastGear: Int = 1
    private var lastRpm: Float = 0f
    private var lastHp: Float = 0f
    private var lastTorque: Float = 0f
    private var charts: ArrayList<LineChart> = ArrayList()
    private var powerReadings: ArrayList<HashMap<Int, HpTqSet>> = ArrayList(ArrayList())

//    private var hpTqvValues: ArrayList<HpTqValueList> = ArrayList()

    private val forzaDataObserver = Observer<HpTorqueViewModel.HpTqData> { data ->
        // Ignore reverse gear and 'gear shift' which comes back as gear = 11
        if (data.gear < 1 || data.gear > 10) {
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
        showWaitingForData()
        // TODO - Remove debug values
        Handler(Looper.myLooper()!!).postDelayed({
            addHpTorque(HpTorqueViewModel.HpTqData(100f, 423f, 1278f, 1))
            addHpTorque(HpTorqueViewModel.HpTqData(123f, 440f, 1340f, 1))
            addHpTorque(HpTorqueViewModel.HpTqData(134f, 450f, 1598f, 1))
            addHpTorque(HpTorqueViewModel.HpTqData(110f, 309f, 1832f, 1))
            addHpTorque(HpTorqueViewModel.HpTqData(90f, 254f, 2231f, 1))
            addHpTorque(HpTorqueViewModel.HpTqData(300f, 210f, 1300f, 2))
            addHpTorque(HpTorqueViewModel.HpTqData(310f, 220f, 1400f, 2))
            addHpTorque(HpTorqueViewModel.HpTqData(320f, 320f, 1500f, 2))
            addHpTorque(HpTorqueViewModel.HpTqData(320f, 350f, 1600f, 2))
            addHpTorque(HpTorqueViewModel.HpTqData(300f, 210f, 1300f, 3))
            addHpTorque(HpTorqueViewModel.HpTqData(310f, 220f, 1400f, 3))
            addHpTorque(HpTorqueViewModel.HpTqData(320f, 320f, 1500f, 3))
            addHpTorque(HpTorqueViewModel.HpTqData(320f, 350f, 1600f, 3))
            addHpTorque(HpTorqueViewModel.HpTqData(300f, 210f, 1300f, 4))
            addHpTorque(HpTorqueViewModel.HpTqData(310f, 220f, 1400f, 4))
            addHpTorque(HpTorqueViewModel.HpTqData(320f, 320f, 1500f, 4))
            addHpTorque(HpTorqueViewModel.HpTqData(320f, 350f, 1600f, 4))
        }, 3000)
        return view
    }

    override fun onResume() {
        super.onResume()
        attachObservers()

        if(charts.isEmpty()){
            showWaitingForData()
        } else {
            showCurves()
        }

    }

    override fun onStop() {
        super.onStop()
        removeObservers()
    }

    private fun showWaitingForData() {
        view.findViewById<LinearLayout>(R.id.hpTorque_waitForDataLayout)
            .visibility = VISIBLE
        view.findViewById<ScrollView>(R.id.hpTorque_scrollView)
            .visibility = GONE
    }

    private fun showCurves() {
        view.findViewById<LinearLayout>(R.id.hpTorque_waitForDataLayout)
            .visibility = GONE
        view.findViewById<ScrollView>(R.id.hpTorque_scrollView)
            .visibility = VISIBLE
    }

    private fun getChartForGear(gear: Int): LineChart {
        fun addLineGraphToView(lineGraph: LineChart) {
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                resources.getDimension(R.dimen.hpTorque_lineChartHeight).toInt()
            )
            params.setMargins(0,20,0,20)
            view.findViewById<LinearLayout>(R.id.hpTorque_curvesRoot)
                .addView(lineGraph, params)
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

    private fun invalidateChart(gear: Int) {
        if(!isShowingCurves) {
            showCurves()
        }
        val powerForGear = powerReadings[gear - 1]
        fun valueList(hp: Boolean?): ArrayList<Entry> {
            val result = ArrayList<Entry>()
            val sortedKeys = powerForGear.keys.sorted()
            sortedKeys.forEach { key ->
                var toAdd = powerForGear[key]!!.tq
                if (hp == true) toAdd = powerForGear[key]!!.hp
                result.add(
                    Entry(key.toFloat(), toAdd)
                )
            }
            return result;
        }

        val chart = getChartForGear(gear)
        val hpDataSet = chart.data.dataSets[0] as LineDataSet
        val torqueDataSet = chart.data.dataSets[1] as LineDataSet

        hpDataSet.values = valueList(true)
        torqueDataSet.values = valueList(false)

        chart.invalidate()
        chart.data.notifyDataChanged()
        chart.notifyDataSetChanged()
    }

    private fun addHpTorque(dataEntry: HpTorqueViewModel.HpTqData) {
        if(!isAdded) {
            return
        }
        while (powerReadings.size < dataEntry.gear) {
            powerReadings.add(HashMap())
        }
        val roundedRpm = roundToNearestRpmRange(dataEntry.rpm)
        Log.d(_tag, "Rounded rpm ${dataEntry.rpm} -> $roundedRpm")
        if (dataEntry.hp < 0) {
            Log.d(_tag, "Ignore 0 horsepower packet")
            return
        }
        val powerForGear = powerReadings[dataEntry.gear - 1]

        if (!powerForGear.containsKey(roundedRpm)) {
            powerForGear[roundedRpm] = HpTqSet(dataEntry.hp, dataEntry.tq)
        } else {
            val existing = powerForGear[roundedRpm]
            if (existing?.tq != dataEntry.tq)
                powerForGear[roundedRpm] = HpTqSet(dataEntry.hp, dataEntry.tq)
            if (existing?.hp != dataEntry.hp)
                powerForGear[roundedRpm] = HpTqSet(dataEntry.hp, dataEntry.tq)
        }
        invalidateChart(dataEntry.gear)
    }

    private fun roundToNearestRpmRange(rpm: Float): Int {
        return Math.round(rpm / 100.0).toInt() * 100
    }

    /*
        private fun addHpTorque(dataEntry: HpTorqueViewModel.HpTqData) {
            while (hpTqvValues.size < dataEntry.gear) {
                hpTqvValues.add(HpTqValueList())
            }
            val roundedRpm = Math.round(dataEntry.rpm)
            if(dataEntry.hp < 0) {
                Log.d(_tag, "Ignore 0 horsepower packet")
                return
            }

            val hpTqValueList = hpTqvValues[dataEntry.gear - 1]
            if(hpTqValueList.hpValues.isNotEmpty()) {
                val firstRpmReading = hpTqValueList.hpValues.first().x
                val lastRpmReading = hpTqValueList.hpValues.last().x
                if(lastRpmReading > roundedRpm && firstRpmReading > roundedRpm){
                    Log.d(_tag, "Ignore decel in ${dataEntry.gear} @ $lastRpmReading -> ${roundedRpm}")
                    return
                }
                val nextRpm = lastRpmReading + 200;
                if(roundedRpm < nextRpm && roundedRpm > firstRpmReading){
                    return
                }
            }
            Log.d(_tag, "adding ${dataEntry.gear} @ ${dataEntry.rpm}")
            hpTqValueList.hpValues.add(Entry(dataEntry.rpm, dataEntry.hp))
            hpTqValueList.tqValues.add(Entry(dataEntry.rpm, dataEntry.tq))
            val alreadyHaveHp = hpTqValueList.hpValues.find { entry -> entry.x.toInt() == roundedRpm }
            val alreadyHaveTq = hpTqValueList.tqValues.find { entry -> entry.x.toInt() == roundedRpm }
            if(alreadyHaveHp != null && alreadyHaveHp.y < dataEntry.hp
                && alreadyHaveTq != null && alreadyHaveTq.y < dataEntry.tq){
                alreadyHaveHp.y = dataEntry.hp
                alreadyHaveTq.y = dataEntry.tq
            }

            val chart = getChartForGear(dataEntry.gear)
            val hpDataSet = chart.data.dataSets[0] as LineDataSet
            val torqueDataSet = chart.data.dataSets[1] as LineDataSet

            hpDataSet.values = hpTqValueList.hpValues
            torqueDataSet.values = hpTqValueList.tqValues

            chart.invalidate()
            chart.data.notifyDataChanged()
            chart.notifyDataSetChanged()
        }
    */

    private fun initializeLineChart(chart: LineChart, desc: String): LineChart {
        val lineGraph = initializeChartDataSets(chart)
        val textColor = resources.getColor(R.color.hpTorque_text, context?.theme)

        lineGraph.xAxis.textColor = textColor
        lineGraph.xAxis.setDrawGridLines(false)
        lineGraph.axisLeft.textColor =
            resources.getColor(R.color.hpTorque_torqueLine, context?.theme)
        lineGraph.axisLeft.setDrawGridLines(false)

        lineGraph.axisRight.textColor = resources.getColor(R.color.hpTorque_hpLine, context?.theme)
        lineGraph.axisRight.setDrawGridLines(false)
        lineGraph.legend.textColor = textColor

        val graphDesc = Description()
        graphDesc.text = desc
        graphDesc.textColor = textColor
        graphDesc.textSize = resources.getDimension(R.dimen.fontSize_large)
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
        hpDataSet.valueTextColor = resources.getColor(R.color.hpTorque_hpLine, context?.theme)
        hpDataSet.lineWidth = 2f

        val tqEntries = ArrayList<Entry>()
        val tqDataSet = LineDataSet(tqEntries, resources.getString(R.string.generic_torque))
        tqDataSet.color = resources.getColor(R.color.hpTorque_torqueLine, context?.theme)
        tqDataSet.valueTextColor = resources.getColor(R.color.hpTorque_torqueLine, context?.theme)
        tqDataSet.lineWidth = 2f

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
}