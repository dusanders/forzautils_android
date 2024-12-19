package com.example.forzautils.ui.networkError

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class NetworkErrorViewModelFactory(val callback: NetworkErrorViewModel.Callback):
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return NetworkErrorViewModel(callback) as T
    }
}