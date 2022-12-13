package com.lighthouse.presentation.util.permission

import android.Manifest
import android.content.Context
import com.lighthouse.presentation.util.permission.core.PermissionManager

class LocationPermissionManager(context: Context) : PermissionManager(context) {

    override val permissions =
        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
}
