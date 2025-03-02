package com.example.forzautils.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.forzautils.viewModels.themeViewModel.ThemeViewModel

interface AppBarActionHandlers {
  fun onSettingsClick()
  fun onBackClick()
  fun shouldShowBackButton(): Boolean
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForzaAppBar(
  handlers: AppBarActionHandlers?
) {
  val appBarScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
  CenterAlignedTopAppBar(
    title = { },
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
      if(handlers?.shouldShowBackButton() == true) {
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(100.dp))
            .clickable(
              onClick = { handlers?.onBackClick() }
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
            onClick = { handlers?.onSettingsClick() }
          )
          .padding(7.dp)
      ) {
        Icon(
          imageVector = Icons.Rounded.Settings,
          contentDescription = "Settings"
        )
      }
    }
  )
}