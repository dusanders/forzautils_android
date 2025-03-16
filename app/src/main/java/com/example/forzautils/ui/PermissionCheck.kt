package com.example.forzautils.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.Settings
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.example.forzautils.ui.pages.permissions.PermissionPage

@Composable
fun PermissionCheck(
  content: @Composable () -> Unit
) {
  val tag = "PermissionCheck"
  val context = LocalContext.current

  fun checkPermission(): Boolean {
    when (ContextCompat.checkSelfPermission(
      context,
      Manifest.permission.ACCESS_FINE_LOCATION
    )) {
      PackageManager.PERMISSION_GRANTED -> {
        Log.d(tag, "Permission granted")
        return true
      }
    }
    return false
  }

  var continueToApp by remember { mutableStateOf(checkPermission()) }
  var permissionPermanentlyDenied by remember { mutableStateOf(false) }

  //region Lifecycle Observer
  val lifecycleOwner: LifecycleOwner = LocalContext.current as LifecycleOwner
  val lifecycleEventObserver = remember {
    LifecycleEventObserver { _, event ->
      if(event == Lifecycle.Event.ON_RESUME){
        val resumedPermission = checkPermission()
        Log.d(tag, "Lifecycle resumed with permission: $resumedPermission")
        continueToApp = resumedPermission
      }
    }
  }
  LaunchedEffect(lifecycleOwner) {
    lifecycleOwner.lifecycle.addObserver(lifecycleEventObserver)
  }
  //endregion

  //region  One time launcher for permissions
  var permissionLauncher = rememberLauncherForActivityResult(
    ActivityResultContracts.RequestMultiplePermissions()
  ) { isGranted ->
    Log.d(tag, "PermissionLauncher granted: $isGranted")
    val hasAllPerms = isGranted.values.reduce { acc, next -> acc && next }
    if (hasAllPerms) {
      continueToApp = true
    } else {
      permissionPermanentlyDenied = true
    }
  }
  //endregion

  //region Render-time permission check
//  when (ContextCompat.checkSelfPermission(
//    LocalContext.current,
//    Manifest.permission.ACCESS_FINE_LOCATION
//  )) {
//    PackageManager.PERMISSION_GRANTED -> {
//      Log.d(tag, "Permission granted")
//      continueToApp = true
//    }
//
//    PackageManager.PERMISSION_DENIED -> {
//      Log.d(tag, "Permission denied")
//    }
//  }
  //endregion


  if (continueToApp) {
    content()
  } else {
    PermissionPage(
      onRequestPermission = {
        if(!permissionPermanentlyDenied){
          permissionLauncher.launch(
            arrayOf(
              Manifest.permission.ACCESS_FINE_LOCATION,
              Manifest.permission.ACCESS_COARSE_LOCATION
            )
          )
        }
        if (permissionPermanentlyDenied) {
          val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            .apply {
              data = android.net.Uri.fromParts("package", context.packageName, null)
            }
          context.startActivity(intent)
        }
      },
      onDenyPermission = {
        continueToApp = true
      }
    )
  }
}