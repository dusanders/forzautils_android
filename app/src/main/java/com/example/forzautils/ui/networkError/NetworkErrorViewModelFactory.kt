package com.example.forzautils.ui.networkError

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * Factory implementation for {@link NetworkErrorViewModel}
 */
class NetworkErrorViewModelFactory(val callback: NetworkErrorViewModel.Callback)
    : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return NetworkErrorViewModel(callback) as T
    }
}