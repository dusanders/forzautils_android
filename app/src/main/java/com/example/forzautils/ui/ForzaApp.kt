package com.example.forzautils.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.forzautils.ui.pages.landing.LandingPage
import com.example.forzautils.ui.pages.live.LiveViewer
import com.example.forzautils.ui.pages.networkError.NetworkError
import com.example.forzautils.ui.pages.sourceChooser.SourceChooserPage
import com.example.forzautils.ui.pages.splash.SplashPage
import com.example.forzautils.utils.Constants
import com.example.forzautils.viewModels.forzaViewModel.ForzaViewModel
import com.example.forzautils.viewModels.networkInfo.ConnectionStates
import com.example.forzautils.viewModels.networkInfo.NetworkInfoViewModel
import com.example.forzautils.viewModels.themeViewModel.ThemeViewModel

@Composable
fun ForzaApp(
  themeViewModel: ThemeViewModel,
  networkInfoViewModel: NetworkInfoViewModel,
  forzaViewModel: ForzaViewModel,
) {
  val tag = "ForzaApp"

  val navController = rememberNavController()
  val currentRoute by navController.currentBackStackEntryAsState()
    .value?.destination?.route.let {
      rememberUpdatedState(it)
    }
  var appBarActions by remember { mutableStateOf<ForzaAppBarActions?>(null) }
  val connectionState by networkInfoViewModel.connectionState.collectAsState()

  LaunchedEffect(currentRoute) {
    if (currentRoute == Constants.Pages.LANDING) {
      appBarActions?.setShowBackButton(false)
    } else {
      appBarActions?.setShowBackButton(true)
    }
  }

  ForzaAppBar(
    themeViewModel,
    onBackPress = {
      navController.popBackStack()
    }
  ) { actions: ForzaAppBarActions ->
    appBarActions = actions
    Box {
      when (connectionState) {
        ConnectionStates.CONNECTING -> {
          actions.setShowBackButton(false)
          SplashPage()
        }

        ConnectionStates.NO_WIFI -> {
          actions.setShowBackButton(false)
          NetworkError()
        }

        ConnectionStates.FORZA_OPEN -> {
          NavHost(navController = navController, startDestination = Constants.Pages.LANDING) {
            composable(Constants.Pages.LANDING) {
              LandingPage(networkInfoViewModel, forzaViewModel, navController)
            }
            composable(Constants.Pages.SOURCE) {
              SourceChooserPage(
                navigateToReplayViewer = {
                  navController.navigate(Constants.Pages.LIVE_VIEWER)
                },
                navigateToLiveViewer = {
                  navController.navigate(Constants.Pages.LIVE_VIEWER)
                }
              )
            }
            composable(Constants.Pages.LIVE_VIEWER) {
              LiveViewer(actions, forzaViewModel)
            }
          }
        }
      }
    }
  }
}