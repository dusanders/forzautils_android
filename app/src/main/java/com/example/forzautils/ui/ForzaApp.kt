package com.example.forzautils.ui

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.forzautils.ui.components.AppBarFlyout
import com.example.forzautils.ui.pages.landing.LandingPage
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

  var showSettingsFlyout by remember { mutableStateOf(false) }
  val connectionState by networkInfoViewModel.connectionState.collectAsState()

  Scaffold(
    modifier = Modifier.fillMaxSize(),
    topBar = {
      ForzaAppBar(object : AppBarActionHandlers {
        override fun onSettingsClick() {
          showSettingsFlyout = !showSettingsFlyout
        }

        override fun onBackClick() {
          navController.popBackStack()
        }

        override fun shouldShowBackButton(): Boolean {
          Log.d(tag, "current route: ${currentRoute}")
          return currentRoute != null
              && (currentRoute != Constants.Pages.NETWORK_ERROR)
              && (currentRoute != Constants.Pages.LANDING)
              && (currentRoute != Constants.Pages.SPLASH)
        }
      })
    }
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .background(MaterialTheme.colorScheme.background)
    ) {
      Box {
        when (connectionState) {
          ConnectionStates.CONNECTING -> {
            SplashPage()
          }

          ConnectionStates.NO_WIFI -> {
            NetworkError()
          }

          ConnectionStates.FORZA_OPEN -> {
            NavHost(navController = navController, startDestination = Constants.Pages.LANDING) {
              composable(Constants.Pages.LANDING) {
                LandingPage(networkInfoViewModel, forzaViewModel, navController)
              }
              composable(Constants.Pages.SOURCE) {
                SourceChooserPage()
              }
            }
            if (showSettingsFlyout) {
              AppBarFlyout(
                themeViewModel,
                onBackgroundClick = { showSettingsFlyout = false }
              )
            }
          }
        }
      }
    }
  }
}