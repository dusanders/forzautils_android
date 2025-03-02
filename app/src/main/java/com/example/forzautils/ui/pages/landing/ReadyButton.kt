package com.example.forzautils.ui.pages.landing

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.forzautils.R
import com.example.forzautils.ui.theme.FontSizes
import com.example.forzautils.ui.theme.LetterSpacings

@Composable
fun ReadyButton(
  onButtonClick: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(top = 56.dp),
    horizontalArrangement = Arrangement.Center,
    verticalAlignment = Alignment.CenterVertically
  ) {
    val touchInteractionSource = remember { MutableInteractionSource() }
    Column(
      modifier = Modifier
        .clickable(
          enabled = true,
          onClick = onButtonClick,
          interactionSource = touchInteractionSource,
          indication = null,
        )
        .padding(start = 25.dp, end = 25.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      Box(
        modifier = Modifier
          .border(
            width = Dp.Hairline,
            color = MaterialTheme.colorScheme.onPrimary,
            shape = AbsoluteRoundedCornerShape(100.dp)
          )
          .clip(AbsoluteRoundedCornerShape(100.dp))
          .clickable(
            onClick = onButtonClick,
            interactionSource = touchInteractionSource,
            indication = ripple()
          )
      ) {
        Icon(
          modifier = Modifier
            .padding(12.dp),
          imageVector = Icons.Rounded.Check,
          contentDescription = stringResource(R.string.generic_ready)
        )
      }
      Text(
        text = stringResource(id = R.string.generic_ready),
        fontSize = FontSizes.xl,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(top = 16.dp),
        letterSpacing = LetterSpacings.textButton,
        fontWeight = FontWeight.Bold
      )
    }
  }
}