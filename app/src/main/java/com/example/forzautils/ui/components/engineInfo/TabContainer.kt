package com.example.forzautils.ui.components.engineInfo

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.forzautils.R
import com.example.forzautils.ui.components.TextCardBox
import com.example.forzautils.ui.theme.FontSizes

@Composable
fun TabContainer(
  title: String? = null,
  contentMap: Map<String, @Composable () -> Unit>
) {
  var selectedIndex by remember { mutableStateOf("") }
  var content by remember { mutableStateOf<(@Composable () -> Unit)?>(null) }

  LaunchedEffect(selectedIndex) {
    Log.d("TabContainer", "selectedIndex changed: $selectedIndex")
    content = contentMap[selectedIndex]
  }

  LaunchedEffect(contentMap) {
    Log.d("TabContainer", "contentMap changed: $contentMap")
    if(contentMap.isNotEmpty()) {
      selectedIndex = contentMap.keys.first()
    }
  }

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(bottom = 12.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(start = 12.dp, end = 12.dp, top = 12.dp)
        .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
        .background(MaterialTheme.colorScheme.surface)
    ) {
      if (title != null) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
        ) {
          Text(
            modifier = Modifier
              .fillMaxWidth(),
            text = title,
            fontSize = FontSizes.xl,
            textAlign = TextAlign.Center
          )
        }
      }
      Box(
        modifier = Modifier
          .padding(12.dp)
      ) {
        if(selectedIndex.isNotEmpty()) {
          contentMap[selectedIndex]?.invoke()
        }
      }
    }
    Row(
      modifier = Modifier
        .padding(bottom = 12.dp, start = 12.dp, end = 12.dp)
    ) {
      if (contentMap.isNotEmpty()) {
        contentMap.keys.map {
          Box(
            modifier = Modifier
              .weight(1f)
          ) {
            TextCardBox(
              padding = 4.dp,
              value = it,
              onClicked = {
                selectedIndex = it
              }
            )
          }
        }
      } else {
        TextCardBox(
          value = stringResource(R.string.tabContainer_noData)
        )
      }
    }
  }
}