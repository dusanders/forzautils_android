package com.example.forzautils.ui

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.forzautils.services.ForzaService
import com.example.forzautils.services.WiFiService
import com.example.forzautils.ui.pages.landing.LandingPage
import com.example.forzautils.ui.pages.networkError.NetworkError
import com.example.forzautils.ui.pages.splash.SplashPage
import com.example.forzautils.utils.Constants
import com.example.forzautils.viewModels.forzaViewModel.ForzaViewModel
import com.example.forzautils.viewModels.forzaViewModel.ForzaViewModelFactory
import com.example.forzautils.viewModels.networkInfo.NetworkInfoViewModel
import com.example.forzautils.viewModels.networkInfo.NetworkInfoViewModelFactory

@Composable
fun ForzaApp(
  wifiService: WiFiService,
  forzaService: ForzaService
) {
  val tag = "ForzaApp"

  val navController = rememberNavController()

  val networkInfoViewModel: NetworkInfoViewModel =
    viewModel(
      factory = NetworkInfoViewModelFactory(wifiService)
    )
  val forzaViewModel: ForzaViewModel = viewModel(
    factory = ForzaViewModelFactory(forzaService)
  )

  WifiStateWatcher(networkInfoViewModel,
    onNetworkAvailable = {
      navController.navigate(Constants.Pages.SPLASH)
    },
    onNetworkError = {
      navController.navigate(Constants.Pages.NETWORK_ERROR)
    }) {
    NavHost(navController = navController, startDestination = Constants.Pages.SPLASH) {
      composable(Constants.Pages.SPLASH) {
        SplashPage(navController, networkInfoViewModel)
      }
      composable(Constants.Pages.LANDING) {
        LandingPage(networkInfoViewModel, forzaViewModel)
      }
      composable(Constants.Pages.NETWORK_ERROR) {
        NetworkError()
      }
    }
  }
}