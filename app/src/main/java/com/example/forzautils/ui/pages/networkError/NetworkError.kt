package com.example.forzautils.ui.pages.networkError

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.forzautils.R

@Composable
fun NetworkError() {
  Column() {
    Text(text = stringResource(R.string.networkInfo_wifi_error))
    Text(text = stringResource(R.string.networkError_desc))
  }
}