package com.example.forzautils.ui.pages.networkError

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.forzautils.R
import com.example.forzautils.ui.theme.FontSizes

@Composable
fun NetworkError() {
  Column(
    modifier = Modifier
      .fillMaxSize(),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    val defaultTextModifier = Modifier
      .padding(start = 26.dp, end = 26.dp, top = 20.dp)
    Text(
      text = stringResource(R.string.networkError_title),
      fontSize = FontSizes.header,
      fontWeight = FontWeight.Bold,
      textAlign = TextAlign.Center,
      modifier = Modifier
        .padding(bottom = 30.dp)
    )
    Text(
      text = stringResource(R.string.networkInfo_wifi_error),
      textAlign = TextAlign.Center,
      modifier = defaultTextModifier
    )
    Text(
      text = stringResource(R.string.networkError_desc),
      textAlign = TextAlign.Center,
      modifier = defaultTextModifier
    )
  }
}