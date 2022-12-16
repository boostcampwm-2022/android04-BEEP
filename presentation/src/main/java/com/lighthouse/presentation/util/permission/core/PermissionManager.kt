package com.lighthouse.presentation.util.permission.core

import android.app.Activity
import android.content.pm.PackageManager
import kotlinx.coroutines.flow.MutableStateFlow

abstract class PermissionManager(
    private val activity: Activity
) {
    abstract val permissions: Array<String>

    open val additionalPermission = emptyArray<String>()

    val basicPermission
        get() = permissions.firstOrNull() ?: ""

    val permissionFlow by lazy {
        MutableStateFlow(isGrant)
    }

    val isGrant
        get() = permissions.all { permission ->
            checkPermission(permission)
        }

    val permissionState
        get() = when {
            (permissions + additionalPermission).all { checkPermission(it) } -> BeepPermissionState.AllAllowedPermission
            permissions.all { checkPermission(it) } -> BeepPermissionState.PartiallyAllowedPermission
            else -> BeepPermissionState.NotAllowedPermission
        }

    private fun checkPermission(permission: String) =
        activity.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
}
