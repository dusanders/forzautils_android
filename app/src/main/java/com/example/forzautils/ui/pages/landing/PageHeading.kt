package com.example.forzautils.ui.pages.landing

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.forzautils.R
import com.example.forzautils.ui.theme.FontSizes

@Composable
fun DescText(
  text: String,
  fontWeight: FontWeight = FontWeight.Normal,
  color: Color = MaterialTheme.colorScheme.primary,
  fontSize: TextUnit = FontSizes.md,
  fontStyle: FontStyle = FontStyle.Normal,
  textAlign: TextAlign = TextAlign.Center,
  lineHeight: TextUnit = FontSizes.xl,
  letterSpacing: TextUnit = 0.sp
) {
  Text(
    text = text,
    modifier = Modifier
      .padding(top = 2.dp),
    color = color,
    fontSize = fontSize,
    fontWeight = fontWeight,
    fontStyle = fontStyle,
    textAlign = textAlign,
    lineHeight = lineHeight,
    letterSpacing = letterSpacing
  )
}


@Composable
fun PageHeading() {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(start = 36.dp, end = 36.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Box(
      modifier = Modifier
        .height(56.dp)
    )
    Text(
      text = stringResource(R.string.landingPage_forwardDataHeading),
      fontWeight = FontWeight.Bold,
      fontSize = FontSizes.xl
    )
    DescText(
      text = stringResource(R.string.landingPage_forwardDataDesc),
      textAlign = TextAlign.Start,
      letterSpacing = 0.9.sp,
      lineHeight = 22.sp
    )
    Text(
      modifier = Modifier.padding(top = 2.dp),
      text = stringResource(R.string.generic_note),
      color = MaterialTheme.colorScheme.onPrimary,
      fontWeight = FontWeight.Bold,
      fontSize = FontSizes.md
    )
    Text(
      text = stringResource(R.string.landingPage_simHub_note),
      color = MaterialTheme.colorScheme.onPrimary,
      fontStyle = FontStyle.Italic,
      fontSize = FontSizes.sm,
      lineHeight = 18.sp,
      textAlign = TextAlign.Center,
      modifier = Modifier
        .padding(top = 0.dp)
    )
  }
}