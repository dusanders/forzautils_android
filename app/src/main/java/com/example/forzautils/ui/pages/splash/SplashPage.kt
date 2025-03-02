package com.example.forzautils.ui.pages.splash

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.forzautils.R
import com.example.forzautils.utils.Constants
import com.example.forzautils.viewModels.networkInfo.NetworkInfoViewModel

@Composable
fun SplashPage(navController: NavController, networkInfoViewModel: NetworkInfoViewModel) {
  val tag = "SplashPage"
  val inetError by networkInfoViewModel.inetError.collectAsState()
  LaunchedEffect(inetError) {
    Log.d(tag, "inetError: $inetError")
    if (inetError) {
      navController.navigate(Constants.Pages.NETWORK_ERROR)
    } else {
      navController.navigate(Constants.Pages.LANDING)
    }
  }

  Column(
    modifier = Modifier
      .fillMaxSize(),
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Text(
      text = stringResource(R.string.splash_appName).toUpperCase(Locale.current),
      textAlign = TextAlign.Center,
      fontSize = 32.sp,
      fontWeight = FontWeight.Bold,
      letterSpacing = 4f.sp
    )
  }
}