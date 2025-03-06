package com.example.forzautils.ui.pages.landing

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.dp
import com.example.forzautils.R
import com.example.forzautils.ui.components.CardBox
import com.example.forzautils.ui.components.TextCardBox
import com.example.forzautils.viewModels.networkInfo.NetworkInfoViewModel
import com.example.forzautils.ui.theme.FontSizes


@Composable
fun WifiInfoTable(networkInfoViewModel: NetworkInfoViewModel) {
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
        value = networkInfoViewModel.getInetInfo().ip,
        label = stringResource(R.string.generic_ipLabel)
      )
      TextCardBox(
        value = networkInfoViewModel.getInetInfo().port.toString(),
        label = stringResource(R.string.generic_portLabel)
      )
    }
    Column(
      modifier = Modifier
        .weight(1f)
    ) {
      TextCardBox(
        value = networkInfoViewModel.getInetInfo().ssid,
        label = stringResource(R.string.generic_wifiName)
      )
      TextCardBox(
        value = stringResource(R.string.generic_DASH),
        label = stringResource(R.string.generic_telemetryFormat)
      )
    }
  }
}