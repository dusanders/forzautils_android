package com.example.forzautils.ui

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.forzautils.services.ForzaService
import com.example.forzautils.services.WiFiService
import com.example.forzautils.ui.components.AppBarFlyout
import com.example.forzautils.ui.pages.landing.LandingPage
import com.example.forzautils.ui.pages.networkError.NetworkError
import com.example.forzautils.ui.pages.sourceChooser.SourceChooserPage
import com.example.forzautils.utils.Constants
import com.example.forzautils.viewModels.forzaViewModel.ForzaViewModel
import com.example.forzautils.viewModels.forzaViewModel.ForzaViewModelFactory
import com.example.forzautils.viewModels.networkInfo.NetworkInfoViewModel
import com.example.forzautils.viewModels.networkInfo.NetworkInfoViewModelFactory
import com.example.forzautils.viewModels.themeViewModel.ThemeViewModel

@Composable
fun ForzaApp(
  themeViewModel: ThemeViewModel,
  wifiService: WiFiService,
  forzaService: ForzaService
) {
  val tag = "ForzaApp"

  val navController = rememberNavController()
  val currentRoute by navController.currentBackStackEntryAsState().value?.destination?.route.let {
    rememberUpdatedState(it)
  }

  val networkInfoViewModel = viewModel<NetworkInfoViewModel>(
    factory = NetworkInfoViewModelFactory(wifiService)
  )
  val forzaViewModel = viewModel<ForzaViewModel>(
    factory = ForzaViewModelFactory(forzaService)
  )

  var showSettingsFlyout by remember { mutableStateOf(false) }

  WifiStateWatcher(networkInfoViewModel,
    onNetworkAvailable = {
      navController.navigate(Constants.Pages.LANDING)
    },
    onNetworkError = {
      navController.navigate(Constants.Pages.NETWORK_ERROR)
    }) {
    Scaffold(
      modifier = Modifier.fillMaxSize(),
      topBar = {
        ForzaAppBar(object: AppBarActionHandlers {
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
          NavHost(navController = navController, startDestination = Constants.Pages.LANDING) {
            composable(Constants.Pages.LANDING) {
              LandingPage(networkInfoViewModel, forzaViewModel, navController)
            }
            composable(Constants.Pages.NETWORK_ERROR) {
              NetworkError()
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