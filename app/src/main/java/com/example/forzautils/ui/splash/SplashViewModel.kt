package com.example.forzautils.ui.splash

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.forzautils.R

class SplashViewModel : ViewModel() {

    enum class LoadingState {
        LOADING,
        FINISHED
    }

    private val _loadingState: MutableLiveData<LoadingState> = MutableLiveData(LoadingState.LOADING)

    val loadingState: LiveData<LoadingState> get() = _loadingState

    fun setLoadingState(state: LoadingState) {
        _loadingState.postValue(state)
    }
}