package com.example.forzautils.ui.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

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