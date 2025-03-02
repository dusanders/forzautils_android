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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.dp
import com.example.forzautils.ui.components.CardBox
import com.example.forzautils.viewModels.networkInfo.NetworkInfoViewModel
import com.example.forzautils.ui.theme.FontSizes

@Composable
fun ValueText(text: String) {
  Text(
    text = text,
    color = MaterialTheme.colorScheme.primary,
    fontSize = FontSizes.md,
    fontWeight = FontWeight.Bold
  )
}
@Composable
fun LabelText(text: String) {
  Text(
    text = text.toUpperCase(Locale.current),
    color = MaterialTheme.colorScheme.onPrimary,
    fontSize = FontSizes.sm,
  )
}
@Composable
fun WifiInfoTable(networkInfoViewModel: NetworkInfoViewModel) {
  val inetInfo by networkInfoViewModel.inetInfo.collectAsState()
  Row(
    modifier = Modifier
      .padding(top = 36.dp)
      .fillMaxWidth()
  ) {

    Column(
      modifier = Modifier
        .weight(1f)
    ) {
      CardBox {
        ValueText(text = inetInfo.ip)
        LabelText(text = "IP Address")
      }
      CardBox {
        ValueText(text = inetInfo.port.toString())
        LabelText(text = "Port")
      }
    }
    Column(
      modifier = Modifier
        .weight(1f)
    ) {
      CardBox {
        ValueText(text = inetInfo.ssid)
        LabelText(text = "WiFi Name")
      }
      CardBox {
        ValueText(text = "DASH")
        LabelText(text = "Telemetry Format")
      }
    }
  }
}