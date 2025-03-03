//package com.example.forzautils.ui.dataViewer.hpTorque
//
//import androidx.lifecycle.LiveData
//import androidx.lifecycle.MutableLiveData
//import androidx.lifecycle.Observer
//import androidx.lifecycle.ViewModel
//import com.example.forzautils.services.ForzaService
//import forza.telemetry.data.TelemetryData
//
//// Alias these for easier code readability
//typealias Gear = Int
//typealias RPM = Int
//
//class HpTorqueViewModel : ViewModel() {
//    interface Callback {
//        fun onBackClicked()
//    }
//
//    data class DataUpdate(
//        var gear: Int,
//        var rpm: Int
//    )
//
//    data class DataForRPM(
//        var hp: Float = 0f,
//        var tq: Float = 0f
//    )
//
//    private data class DataPacket (
//        var gear: Int,
//        var rpm: Float,
//        var horsePower: Float,
//        var torque: Float
//    )
//
//    private val _tag = "HpTorqueViewModel"
//    private lateinit var forzaService: ForzaService
//    private var callback: Callback? = null
//
//    private val _dataUpdated: MutableLiveData<DataUpdate> = MutableLiveData()
//    val dataUpdated: LiveData<DataUpdate> get() = _dataUpdated
//
//    val dataMap: HashMap<Gear, HashMap<RPM, DataForRPM>> = HashMap()
//
//    private val dataObserver: Observer<TelemetryData?> = Observer { data ->
//        parseData(data)
//    }
//
//    override fun onCleared() {
//        super.onCleared()
//        removeObservers()
//    }
//
//    fun clear() {
//        dataMap.clear()
//    }
//
//    fun setCallback(callback: Callback) {
//        this.callback = callback
//    }
//
//    fun onBackClick() {
//        callback?.onBackClicked()
//    }
//
//    fun setForzaService(forzaService: ForzaService) {
//        this.forzaService = forzaService
//        attachObservers()
//    }
//
//    fun hpForRpm(rpm: Float): Float {
//        return (rpm * 0.08f)
//    }
//    fun tqForRpm(rpm: Float): Float {
//        val percent = rpm / 6800;
//        if(percent <= 0.1){
//            return rpm * 0.01f;
//        }
//        if(percent <= 0.2) {
//            return rpm * 0.014f;
//        }
//        if(percent <= 0.3){
//            return rpm * 0.018f;
//        }
//        if(percent <= 0.4){
//            return rpm * 0.02f
//        }
//        if(percent <= 0.5){
//            return rpm * 0.021f
//        }
//        if(percent <= 0.6){
//            return rpm * 0.0215f
//        }
//        if(percent <= 0.7){
//            return rpm * 0.014f
//        }
//        if(percent <= 0.8){
//            return rpm * 0.012f
//        }
//        if(percent <= 0.9){
//            return rpm * 0.010f
//        }
//        return rpm * 0.009f
//    }
//    private fun dataForGear(gear: Int, startRpm: Int): ArrayList<DataPacket> {
//        var result = ArrayList<DataPacket>();
//        for(i in startRpm..6800 step 200) {
//            result.add(
//                DataPacket(
//                rpm = i.toFloat(),
//                gear = gear,
//                horsePower = hpForRpm(i.toFloat()),
//                torque = tqForRpm(i.toFloat())
//            ))
//        }
//        return result
//    }
//    fun DEBUG() {
//        var g1 = dataForGear(1, 800)
//        for(i in g1) {
//            DEBUG_processDataPacket(i)
//        }
//
//        var g2 = dataForGear(2, 3200)
//        for(i in g2){
//            DEBUG_processDataPacket(i)
//        }
//        var g3 = dataForGear(3, 3300)
//        for(i in g3){
//            DEBUG_processDataPacket(i)
//        }
//        var g4 = dataForGear(4, 4200)
//        for(i in g4){
//            DEBUG_processDataPacket(i)
//        }
//        var g5 = dataForGear(5, 4200)
//        for(i in g5){
//            DEBUG_processDataPacket(i)
//        }
//        /**
//        DEBUG_processDataPacket(DataPacket(
//            rpm = 1232f,
//            gear = 1,
//            horsePower = 123f,
//            torque = tqForRpm(1232f)
//        ))
//        DEBUG_processDataPacket(DataPacket(
//            rpm = 1332f,
//            gear = 1,
//            horsePower = 133f,
//            torque = 111f
//        ))
//        DEBUG_processDataPacket(DataPacket(
//            rpm = 1432f,
//            gear = 1,
//            horsePower = 143f,
//            torque = 120f
//        ))
//        DEBUG_processDataPacket(DataPacket(
//            rpm = 1532f,
//            gear = 1,
//            horsePower = 153f,
//            torque = 130f
//        ))
//        DEBUG_processDataPacket(DataPacket(
//            rpm = 1832f,
//            gear = 1,
//            horsePower = 163f,
//            torque = 140f
//        ))
//        DEBUG_processDataPacket(DataPacket(
//            rpm = 2232f,
//            gear = 1,
//            horsePower = 183f,
//            torque = 160f
//        ))
//        DEBUG_processDataPacket(DataPacket(
//            rpm = 2532f,
//            gear = 1,
//            horsePower = 193f,
//            torque = 180f
//        ))
//        DEBUG_processDataPacket(DataPacket(
//            rpm = 2832f,
//            gear = 1,
//            horsePower = 233f,
//            torque = 200f
//        ))
//        DEBUG_processDataPacket(DataPacket(
//            rpm = 3132f,
//            gear = 1,
//            horsePower = 283f,
//            torque = 210f
//        ))
//        DEBUG_processDataPacket(DataPacket(
//            rpm = 3332f,
//            gear = 1,
//            horsePower = 333f,
//            torque = 220f
//        ))
//        DEBUG_processDataPacket(DataPacket(
//            rpm = 3532f,
//            gear = 1,
//            horsePower = 353f,
//            torque = 223f
//        ))
//        DEBUG_processDataPacket(DataPacket(
//            rpm = 3832f,
//            gear = 1,
//            horsePower = 393f,
//            torque = 224f
//        ))
//        DEBUG_processDataPacket(DataPacket(
//            rpm = 4232f,
//            gear = 1,
//            horsePower = 403f,
//            torque = 223f
//        ))
//        DEBUG_processDataPacket(DataPacket(
//            rpm = 4432f,
//            gear = 1,
//            horsePower = 413f,
//            torque = 220f
//        ))
//        DEBUG_processDataPacket(DataPacket(
//            rpm = 4632f,
//            gear = 1,
//            horsePower = 423f,
//            torque = 210f
//        ))
//        DEBUG_processDataPacket(DataPacket(
//            rpm = 4832f,
//            gear = 1,
//            horsePower = 425f,
//            torque = 190f
//        ))
//        DEBUG_processDataPacket(DataPacket(
//            rpm = 5132f,
//            gear = 1,
//            horsePower = 423f,
//            torque = 180f
//        ))
//        DEBUG_processDataPacket(DataPacket(
//            rpm = 5332f,
//            gear = 1,
//            horsePower = 413f,
//            torque = 170f
//        ))
//        DEBUG_processDataPacket(DataPacket(
//            rpm = 5532f,
//            gear = 1,
//            horsePower = 403f,
//            torque = 160f
//        ))
//        DEBUG_processDataPacket(DataPacket(
//            rpm = 6232f,
//            gear = 1,
//            horsePower = 393f,
//            torque = 150f
//        ))
//        DEBUG_processDataPacket(DataPacket(
//            rpm = 6432f,
//            gear = 1,
//            horsePower = 383f,
//            torque = 140f
//        ))
//        DEBUG_processDataPacket(DataPacket(
//            rpm = 6832f,
//            gear = 1,
//            horsePower = 373f,
//            torque = 130f
//        ))
//        **/
//    }
//
//    private fun attachObservers() {
//        forzaService.data.observeForever(dataObserver)
//    }
//
//    private fun removeObservers() {
//        forzaService.data.removeObserver(dataObserver)
//    }
//
//    private fun DEBUG_processDataPacket(incoming: DataPacket) {
//        val data = incoming!!
//        val roundedRpm = roundToNearestRpmRange(data.rpm)
//        if (!dataMap.containsKey(data.gear)) {
//            dataMap[data.gear] = HashMap()
//        }
//        val gearMap = dataMap[data.gear]
//        if (!gearMap!!.containsKey(roundedRpm)) {
//            gearMap[roundedRpm] = DataForRPM(
//                data.horsePower,
//                data.torque
//            )
//        } else {
//            val existingValues = gearMap[roundedRpm]
//            if (existingValues!!.hp < data.horsePower) {
//                gearMap[roundedRpm]!!.hp = data.horsePower
//            }
//            if (existingValues.tq < data.torque) {
//                gearMap[roundedRpm]!!.tq = data.torque
//            }
//        }
//        val eventData = DataUpdate(data.gear, roundedRpm)
//        _dataUpdated.value = eventData
//    }
//
//    private fun parseData(incoming: TelemetryData?) {
//        // Check for invalid data
//        if (isInvalidData(incoming)) {
//            return;
//        }
//        // We checked against null in the above check
//        val data = incoming!!
//        val roundedRpm = roundToNearestRpmRange(data.currentEngineRpm.toFloat())
//        if (!dataMap.containsKey(data.gear)) {
//            dataMap[data.gear] = HashMap()
//        }
//        val gearMap = dataMap[data.gear]
//        if (!gearMap!!.containsKey(roundedRpm)) {
//            gearMap[roundedRpm] = DataForRPM(
//                data.horsepower().toFloat(),
//                data.torque.toFloat()
//            )
//        } else {
//            val existingValues = gearMap[roundedRpm]
//            if (existingValues!!.hp < data.horsepower()) {
//                gearMap[roundedRpm]!!.hp = data.horsepower().toFloat()
//            }
//            if (existingValues.tq < data.torque) {
//                gearMap[roundedRpm]!!.tq = data.torque.toFloat()
//            }
//        }
//        val eventData = DataUpdate(data.gear, roundedRpm)
//        _dataUpdated.value = eventData
//    }
//
//    private fun isInvalidData(data: TelemetryData?): Boolean {
//        // We skip null data, gears less than first, and
//        // Forza sends gear == 11 as a 'shift' event
//        return (data == null
//                || data.gear < 1
//                || data.gear > 10
//                // We skip data readings of negative values (usually decel)
//                || data.horsepower() <= 0
//                || data.torque <= 0
//                // Skip partial throttle events - only looking for full hp
//                || data.throttle < 95
//                // Skip power readings during a clutch event
//                || data.clutch > 0)
//    }
//
//    private fun roundToNearestRpmRange(rpm: Float): Int {
//        return Math.round(rpm / 100.0).toInt() * 100
//    }
//}