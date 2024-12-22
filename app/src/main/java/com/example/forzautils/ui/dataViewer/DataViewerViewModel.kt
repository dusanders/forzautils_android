package com.example.forzautils.ui.dataViewer

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.forzautils.services.ForzaService

class DataViewerViewModel(private val forzaService: ForzaService) : ViewModel() {
    enum class DataDisplay {
        OPTIONS_LIST,
        HP_TORQUE
    }

    private val _tag = "DataViewerViewModel"
    private val _currentDataDisplay: MutableLiveData<DataDisplay> = MutableLiveData(DataDisplay.OPTIONS_LIST)
    val currentDataDisplay: LiveData<DataDisplay> get() = _currentDataDisplay

    fun setDataDisplay(display: DataDisplay) {
        Log.d(_tag, "Update display to $display")
        _currentDataDisplay.postValue(display)
    }

}