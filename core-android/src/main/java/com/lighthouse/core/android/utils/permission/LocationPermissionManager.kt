package com.lighthouse.core.android.utils.permission

import android.Manifest
import android.app.Activity
import android.os.Build
import com.lighthouse.core.android.utils.permission.core.PermissionManager

class LocationPermissionManager(activity: Activity) : PermissionManager(activity) {

    override val additionalPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
    } else {
        emptyArray()
    }

    override val permissions =
        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
}
