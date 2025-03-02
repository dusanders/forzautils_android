package com.example.forzautils.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.forzautils.viewModels.networkInfo.NetworkInfoViewModel

@Composable
fun WifiStateWatcher(networkInfoViewModel: NetworkInfoViewModel,
                     onNetworkError: () -> Unit,
                     onNetworkAvailable: () -> Unit,
                     content: @Composable () -> Unit) {
  val networkError by networkInfoViewModel.inetError.collectAsState()
  LaunchedEffect(networkError) {
    if(networkError) {
      onNetworkError()
    } else {
      onNetworkAvailable()
    }
  }
  content()
}