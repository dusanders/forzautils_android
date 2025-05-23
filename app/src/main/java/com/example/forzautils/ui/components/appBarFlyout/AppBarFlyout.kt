package com.example.forzautils.ui.components.appBarFlyout

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.forzautils.viewModels.theme.ThemeViewModel

@Composable
fun AppBarFlyout(
  themeViewModel: ThemeViewModel,
  onBackgroundClick: () -> Unit,
  additionalContent: List<@Composable () -> Unit>
) {
  val tag = "AppBarFlyout"

  Column(
    modifier = Modifier
      .fillMaxSize()
      .clickable(
        onClick = { onBackgroundClick() }
      )
      .background(Color.Black.copy(alpha = 0.3f)),
    horizontalAlignment = Alignment.End
  ) {
    Column(
      modifier = Modifier
        .widthIn(155.dp, 195.dp)
        .clickable(
          onClick = { /*ignore*/ },
          enabled = false
        )
        .clip(RoundedCornerShape(10.dp))
        .background(MaterialTheme.colorScheme.surface)
        .padding(start = 20.dp, end= 20.dp, top = 5.dp, bottom = 5.dp)
    ) {
      LightModeSwitch(themeViewModel)
      additionalContent.forEach {
        it()
      }
    }
  }
}