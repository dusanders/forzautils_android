package com.example.forzautils.ui.pages.landing

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.forzautils.viewModels.forzaViewModel.ForzaViewModel
import com.example.forzautils.viewModels.networkInfo.NetworkInfoViewModel

@Composable
fun LandingPage(
  networkInfoViewModel: NetworkInfoViewModel,
  forzaViewModel: ForzaViewModel
) {
  val tag = "LandingPage"
  val forzaListening by forzaViewModel.listening.collectAsState()
  Log.d(tag, "Loading with ${forzaListening}")
  Column(
    modifier = Modifier
      .fillMaxSize()
      .verticalScroll(rememberScrollState())
  ) {
    PageHeading()
    WifiInfoTable(networkInfoViewModel)
    if(forzaListening) {
      ReadyButton(
        onButtonClick = {
          Log.d(tag, "Ready button clicked")
        }
      )
    }
  }
}