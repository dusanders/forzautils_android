package com.example.forzautils.ui.pages.landing

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.example.forzautils.R
import com.example.forzautils.ui.components.PageHeading
import com.example.forzautils.utils.Constants
import com.example.forzautils.viewModels.forzaViewModel.ForzaViewModel
import com.example.forzautils.viewModels.networkInfo.NetworkInfoViewModel

@Composable
fun LandingPage(
  networkInfoViewModel: NetworkInfoViewModel,
  forzaViewModel: ForzaViewModel,
  navController: NavController
) {
  val tag = "LandingPage"
  val forzaListening by forzaViewModel.listening.collectAsState()
  Log.d(tag, "Loading with ${forzaListening}")
  Column(
    modifier = Modifier
      .fillMaxSize()
      .verticalScroll(rememberScrollState())
  ) {
    PageHeading(
      title = stringResource(R.string.landingPage_forwardDataHeading),
      desc = stringResource(R.string.landingPage_forwardDataDesc),
      noteHeading = stringResource(R.string.generic_note),
      note = stringResource(R.string.landingPage_simHub_note)
    )
    WifiInfoTable(networkInfoViewModel)
    if(forzaListening) {
      ReadyButton(
        onButtonClick = {
          Log.d(tag, "Ready button clicked")
          navController.navigate(Constants.Pages.SOURCE)
        }
      )
    }
  }
}