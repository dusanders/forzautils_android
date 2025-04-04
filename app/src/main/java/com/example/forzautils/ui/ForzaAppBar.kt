package com.example.forzautils.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.forzautils.R
import com.example.forzautils.ui.components.appBarFlyout.AppBarFlyout
import com.example.forzautils.viewModels.theme.ThemeViewModel

interface ForzaAppBarActions {
  fun setShowBackButton(show: Boolean)
  fun setTitleElement(element: @Composable () -> Unit)
  fun removeTitleElement()
  fun injectElement(element: @Composable () -> Unit): String
  fun removeElement(id: String)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForzaAppBar(
  themeViewModel: ThemeViewModel,
  onBackPress: () -> Unit,
  content: @Composable (actions: ForzaAppBarActions) -> Unit
) {
  val appBarScrollBehavior = TopAppBarDefaults
    .enterAlwaysScrollBehavior(rememberTopAppBarState())

  var showSettingsFlyout by remember { mutableStateOf(false) }
  var showBackButton by remember { mutableStateOf(true) }
  var injectedElements by remember { mutableStateOf(listOf<@Composable () -> Unit>()) }
  var appBarTitle by remember { mutableStateOf<@Composable () -> Unit>({ }) }

  val actions: ForzaAppBarActions = remember {
    object : ForzaAppBarActions {
      override fun setShowBackButton(show: Boolean) {
        showBackButton = show
      }

      override fun setTitleElement(element: @Composable () -> Unit) {
        appBarTitle = element
      }

      override fun removeTitleElement() {
        appBarTitle = { }
      }

      override fun injectElement(
        element: @Composable () -> Unit
      ): String {
        injectedElements += element
        return element.hashCode().toString()
      }

      override fun removeElement(id: String) {
        injectedElements = injectedElements.filter { it.hashCode().toString() != id }
      }
    }
  }

  Scaffold(
    modifier = Modifier.fillMaxSize(),
    topBar = {
      CenterAlignedTopAppBar(
        title = { appBarTitle() },
        scrollBehavior = appBarScrollBehavior,
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = MaterialTheme.colorScheme.background,
          titleContentColor = MaterialTheme.colorScheme.primary,
          actionIconContentColor = MaterialTheme.colorScheme.primary,
          navigationIconContentColor = MaterialTheme.colorScheme.onBackground
        ),
        modifier = Modifier
          .padding(end = 16.dp, start = 16.dp),
        navigationIcon = {
          if (showBackButton) {
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(100.dp))
                .clickable(
                  onClick = { onBackPress() }
                )
                .padding(7.dp)
            ) {
              Icon(
                imageVector = Icons.Rounded.ArrowBackIosNew,
                contentDescription = "Back Arrow"
              )
            }
          }
        },
        actions = {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(100.dp))
              .clickable(
                onClick = { showSettingsFlyout = !showSettingsFlyout }
              )
              .padding(7.dp)
          ) {
            Icon(
              imageVector = Icons.Rounded.Settings,
              contentDescription = stringResource(R.string.generic_settings)
            )
          }
        }
      )
    }
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .background(MaterialTheme.colorScheme.background)
    ) {
      key(actions) {
        content(actions)
      }
      if (showSettingsFlyout) {
        AppBarFlyout(
          themeViewModel,
          onBackgroundClick = { showSettingsFlyout = false },
          additionalContent = injectedElements
        )
      }
    }
  }
}