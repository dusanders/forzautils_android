package com.example.forzautils.ui.dataViewer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.forzautils.services.ForzaService

class DataViewerViewModelFactory(val forzaService: ForzaService)
    : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return DataViewerViewModel(forzaService) as T
    }
}