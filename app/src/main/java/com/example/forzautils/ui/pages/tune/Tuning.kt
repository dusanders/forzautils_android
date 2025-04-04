package com.example.forzautils.ui.pages.tune

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.forzautils.R
import com.example.forzautils.ui.ForzaAppBarActions
import com.example.forzautils.ui.components.TextCardBox
import com.example.forzautils.ui.components.TextInput
import com.example.forzautils.utils.toPrecision
import com.example.forzautils.viewModels.tuningViewModel.TuningViewModel

@Composable
fun Tuning(
  appBarActions: ForzaAppBarActions,
  tuningViewModel: TuningViewModel
) {
  val weightInputHint = stringResource(R.string.tuningPage_weightInputHint)
  val weightDistributionInputHintFront =
    stringResource(R.string.tuningPage_weightDistributionInputHint_front)
  val weightDistributionInputHintRear =
    stringResource(R.string.tuningPage_weightDistributionInputHint_rear)
  val weightInputDesc = stringResource(R.string.tuningPage_weightInputDesc)
  val frontWeightDistributionDesc = stringResource(R.string.tuningPage_frontWeightDistributionDesc)
  val rearWeightDistributionDesc = stringResource(R.string.tuningPage_rearWeightDistributionDesc)
  var weightInput by remember { mutableStateOf("") }
  var frontDistroInput by remember { mutableStateOf("") }
  var rearDistroInput by remember { mutableStateOf("") }
  var frontRideHeightInput by remember { mutableStateOf("") }
  var rearRideHeightInput by remember { mutableStateOf("") }
  var hasRollCage by remember { mutableStateOf(false) }
  var drivetrain by remember { mutableStateOf(tuningViewModel.drivetrainTypes[0]) }
  var engineLayout by remember { mutableStateOf(tuningViewModel.engineLayouts[0]) }
  val frontDistribution by tuningViewModel.frontDistribution.collectAsState()
  val rearDistribution by tuningViewModel.rearDistribution.collectAsState()
  val frontHeight by tuningViewModel.frontRideHeight.collectAsState()
  val rearHeight by tuningViewModel.rearRideHeight.collectAsState()
  val totalWeight by tuningViewModel.totalWeight.collectAsState()
  val frontWeight by tuningViewModel.frontWeight.collectAsState()
  val frontCornerWeight by tuningViewModel.frontCornerWeight.collectAsState()
  val rearWeight by tuningViewModel.rearWeight.collectAsState()
  val rearCornerWeight by tuningViewModel.rearCornerWeight.collectAsState()
  val frontSpringRate by tuningViewModel.frontSpringRate.collectAsState()
  val rearSpringRate by tuningViewModel.rearSpringRate.collectAsState()
  val frontBump by tuningViewModel.frontBump.collectAsState()
  val rearBump by tuningViewModel.rearBump.collectAsState()
  val frontRebound by tuningViewModel.frontRebound.collectAsState()
  val rearRebound by tuningViewModel.rearRebound.collectAsState()
  val frontARB by tuningViewModel.frontARB.collectAsState()
  val rearARB by tuningViewModel.rearARB.collectAsState()

  LaunchedEffect(frontDistribution) {
    frontDistroInput = frontDistribution.toString()
  }
  LaunchedEffect(rearDistribution) {
    rearDistroInput = rearDistribution.toString()
  }
  LaunchedEffect(frontHeight) {
    frontRideHeightInput = frontHeight.toString()
  }
  LaunchedEffect(rearHeight) {
    rearRideHeightInput = rearHeight.toString()
  }
  LaunchedEffect(totalWeight) {
    weightInput = totalWeight.toString()
  }
  LaunchedEffect(hasRollCage) {
    tuningViewModel.setRollCage(hasRollCage)
  }
  LaunchedEffect(drivetrain) {
    tuningViewModel.setDrivetrain(drivetrain)
  }
  LaunchedEffect(engineLayout) {
    tuningViewModel.setEngineLayout(engineLayout)
  }
  DisposableEffect(appBarActions) {
    appBarActions.setTitleElement {
      Text(text = "Tuning")
    }
    onDispose {
      appBarActions.removeTitleElement()
    }
  }

  fun setInputValues() {
    tuningViewModel.setFrontRideHeight(frontRideHeightInput)
    tuningViewModel.setRearRideHeight(rearRideHeightInput)
    tuningViewModel.setTotalWeight(weightInput)
  }

  LazyColumn() {
    item {
      Row(modifier = Modifier.height(100.dp)) {
        Box(modifier = Modifier.weight(1f)) {
          TextInput(
            onDonePressed = {
              setInputValues()
            },
            hint = weightInputHint,
            value = weightInput,
            onValueChange = {
              weightInput = it
            },
            desc = weightInputDesc
          )
        }
        Box(modifier = Modifier.weight(1f)) {
          CardSwitch(
            title = "Roll Cage",
            checked = hasRollCage,
            onCheckedChange = { hasRollCage = it }
          )
        }
      }
    }
    item {
      Row() {
        Box(modifier = Modifier.weight(1f)) {
          CardDropdown(
            label = "Drivetrain",
            options = tuningViewModel.drivetrainTypes,
            selectedOption = drivetrain,
            onOptionSelected = {
              drivetrain = it
            }
          )
        }
        Box(modifier = Modifier.weight(1f)) {
          CardDropdown(
            label = "Engine Layout",
            options = tuningViewModel.engineLayouts,
            selectedOption = engineLayout,
            onOptionSelected = {
              engineLayout = it
            }
          )
        }
      }
    }
    item {
      Row() {
        Box(modifier = Modifier.weight(1f)) {
          TextInput(
            onDonePressed = {
              setInputValues()
            },
            hint = weightDistributionInputHintFront,
            value = frontDistroInput,
            onValueChange = {
              frontDistroInput = it
              val updated = it.toFloatOrNull()
              if(updated != null) {
                tuningViewModel.setFrontDistribution(it)
              }
            },
            desc = frontWeightDistributionDesc
          )
        }
        Box(modifier = Modifier.weight(1f)) {
          TextInput(
            onDonePressed = {
              setInputValues()
            },
            hint = weightDistributionInputHintRear,
            value = rearDistroInput,
            onValueChange = {
              rearDistroInput = it
              val updated = it.toFloatOrNull()
              if(updated != null) {
                tuningViewModel.setRearDistribution(it)
              }
            },
            desc = rearWeightDistributionDesc
          )
        }
      }
    }
    item {
      Row() {
        Box(modifier = Modifier.weight(1f)) {
          TextInput(
            onDonePressed = {
              setInputValues()
            },
            hint = "Ride Height",
            value = frontRideHeightInput,
            onValueChange = {
              frontRideHeightInput = it
            },
            desc = "Front Ride Height"
          )
        }
        Box(modifier = Modifier.weight(1f)) {
          TextInput(
            onDonePressed = {
              setInputValues()
            },
            hint = "Ride Height",
            value = rearRideHeightInput,
            onValueChange = {
              rearRideHeightInput = it
            },
            desc = "Rear Ride Height"
          )
        }
      }
    }
    item {
      Row() {
        Box(modifier = Modifier.weight(1f)) {
          TextCardBox(
            label = "Left Front",
            value = frontCornerWeight.toPrecision(2).toString()
          )
        }
        Box(modifier = Modifier.weight(1f)) {
          TextCardBox(
            label = "Total Front",
            value = frontWeight.toPrecision(2).toString()
          )
        }
        Box(modifier = Modifier.weight(1f)) {
          TextCardBox(
            label = "Right Front",
            value = frontCornerWeight.toPrecision(2).toString()
          )
        }
      }
    }
    item {
      Row() {
        Box(modifier = Modifier.weight(1f)) {
          TextCardBox(
            label = "Left Rear",
            value = rearCornerWeight.toPrecision(2).toString()
          )
        }
        Box(modifier = Modifier.weight(1f)) {
          TextCardBox(
            label = "Total Rear",
            value = rearWeight.toPrecision(2).toString()
          )
        }
        Box(modifier = Modifier.weight(1f)) {
          TextCardBox(
            label = "Right Rear",
            value = rearCornerWeight.toPrecision(2).toString()
          )
        }
      }
    }
    item {
      Row() {
        Box(modifier = Modifier.weight(1f)) {
          TextCardBox(
            label = "Front Spring Rate",
            value = frontSpringRate.toPrecision(2).toString()
          )
        }
        Box(modifier = Modifier.weight(1f)) {
          TextCardBox(
            label = "Rear Spring Rate",
            value = rearSpringRate.toPrecision(2).toString()
          )
        }
      }
    }
    item {
      Row() {
        Box(modifier = Modifier.weight(1f)) {
          TextCardBox(
            label = "Front Bump",
            value = frontBump.toPrecision(2).toString()
          )
        }
        Box(modifier = Modifier.weight(1f)) {
          TextCardBox(
            label = "Front Rebound",
            value = frontRebound.toPrecision(2).toString()
          )
        }
      }
      Row() {
        Box(modifier = Modifier.weight(1f)) {
          TextCardBox(
            label = "Rear Bump",
            value = rearBump.toPrecision(2).toString()
          )
        }
        Box(modifier = Modifier.weight(1f)) {
          TextCardBox(
            label = "Rear Rebound",
            value = rearRebound.toPrecision(2).toString()
          )
        }
      }
    }
    item {
      Row() {
        Box(modifier = Modifier.weight(1f)) {
          TextCardBox(
            label = "Front ARB",
            value = frontARB.toPrecision(2).toString()
          )
        }
        Box(modifier = Modifier.weight(1f)) {
          TextCardBox(
            label = "Rear ARB",
            value = rearARB.toPrecision(2).toString()
          )
        }
      }
    }
  }
}