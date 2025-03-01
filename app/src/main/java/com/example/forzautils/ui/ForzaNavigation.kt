package com.example.forzautils.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.forzautils.ui.pages.landing.LandingPage
import com.example.forzautils.ui.pages.networkError.NetworkError
import com.example.forzautils.ui.pages.splash.SplashPage
import com.example.forzautils.utils.Constants
import com.example.forzautils.viewModels.NetworkInfoViewModel

@Composable
fun ForzaNavigation(networkInfoViewModel: NetworkInfoViewModel) {
  val navController = rememberNavController()
  NavHost(navController = navController, startDestination = Constants.Pages.SPLASH) {
    composable(Constants.Pages.SPLASH){
      SplashPage(navController, networkInfoViewModel)
    }
    composable(Constants.Pages.LANDING) {
      LandingPage(networkInfoViewModel)
    }
    composable(Constants.Pages.NETWORK_ERROR) {
      NetworkError()
    }
  }
}