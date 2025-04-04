package com.example.forzautils.ui.pages.permissions

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.forzautils.ui.components.TextCardBox

@Composable
fun PermissionPage(
  onRequestPermission: () -> Unit,
  onDenyPermission: () -> Unit
) {
  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background),
    verticalArrangement = Arrangement.Center
  ) {
    Column(
      modifier = Modifier.fillMaxWidth()
        .padding(12.dp)
    ) {
      Text(
        modifier = Modifier.fillMaxWidth(),
        text = "Permissions",
        color = MaterialTheme.colorScheme.primary,
        textAlign = TextAlign.Center
      )
      Text(
        modifier = Modifier.fillMaxWidth(),
        text = "The app needs to access your location to properly access the WiFi network name.",
        color = MaterialTheme.colorScheme.primary,
        textAlign = TextAlign.Center
      )
    }
    Row() {
      Box(modifier = Modifier.weight(1f)) {
        TextCardBox(
          value = "Grant Permission",
          label = "Allow the app to access your location",
          onClicked = onRequestPermission
        )
      }
      Box(modifier = Modifier.weight(1f)) {
        TextCardBox(
          value = "Deny Permission",
          label = "Deny the app to access your location",
          onClicked = onDenyPermission
        )
      }
    }
  }
}