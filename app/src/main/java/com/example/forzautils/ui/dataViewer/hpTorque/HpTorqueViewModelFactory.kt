package com.example.forzautils.ui.dataViewer.hpTorque

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.forzautils.services.ForzaService

class HpTorqueViewModelFactory(val forzaService: ForzaService)
    : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HpTorqueViewModel(forzaService) as T
    }
}