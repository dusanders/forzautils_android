package com.example.forzautils.ui.pages.landing

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import com.example.forzautils.ui.pages.networkError.NetworkError
import com.example.forzautils.viewModels.NetworkInfoViewModel

@Composable
fun LandingPage(networkInfoViewModel: NetworkInfoViewModel) {
  Column(
    modifier = Modifier
      .fillMaxSize()
      .verticalScroll(rememberScrollState())
  ) {
    PageHeading()
    WifiInfoTable(networkInfoViewModel)
  }
}