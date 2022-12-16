package com.lighthouse.presentation.util.permission

import android.Manifest
import android.app.Activity
import android.os.Build
import androidx.annotation.RequiresApi
import com.lighthouse.presentation.util.permission.core.PermissionManager

class LocationPermissionManager(activity: Activity) : PermissionManager(activity) {

    @RequiresApi(Build.VERSION_CODES.Q)
    override val backGroundPermission =
        arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION)

    override val permissions =
        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
}
