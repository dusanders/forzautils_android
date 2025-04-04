package com.example.forzautils.ui.pages.landing

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.forzautils.R
import com.example.forzautils.ui.components.TextCardBox
import com.example.forzautils.viewModels.networkInfo.ConnectionStates
import com.example.forzautils.viewModels.networkInfo.NetworkInfoViewModel


@Composable
fun WifiInfoTable(networkInfoViewModel: NetworkInfoViewModel) {
  val context = LocalContext.current
  val inetInfo by networkInfoViewModel.inetState.collectAsState()
  val state by networkInfoViewModel.connectionState.collectAsState()
  var connectionString by remember { mutableStateOf("") }
  var ipString by remember { mutableStateOf("") }
  var portString by remember { mutableStateOf("") }

  LaunchedEffect(state) {
    if(state == ConnectionStates.NO_WIFI) {
      connectionString = context.getString(R.string.generic_disconnected)
    } else {
      connectionString = context.getString(R.string.generic_connected)
    }
  }

  LaunchedEffect(inetInfo) {
    ipString = inetInfo?.ip ?: "-"
    portString = inetInfo?.port?.toString() ?: "-"
  }

  Row(
    modifier = Modifier
      .padding(top = 36.dp)
      .fillMaxWidth()
  ) {

    Column(
      modifier = Modifier
        .weight(1f)
    ) {
      TextCardBox(
        value = ipString,
        label = stringResource(R.string.generic_ipLabel)
      )
      TextCardBox(
        value = portString,
        label = stringResource(R.string.generic_portLabel)
      )
    }
    Column(
      modifier = Modifier
        .weight(1f)
    ) {
      TextCardBox(
        value = connectionString,
        label = stringResource(R.string.generic_wifiConnection)
      )
      TextCardBox(
        value = stringResource(R.string.generic_DASH),
        label = stringResource(R.string.generic_telemetryFormat)
      )
    }
  }
}