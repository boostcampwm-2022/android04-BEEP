package com.lighthouse.presentation.util.permission.core

import android.app.Activity
import android.content.Context
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
            activity.checkPermission(permission)
        }

    val permissionState
        get() = when {
            (permissions + additionalPermission).all { activity.checkPermission(it) } -> {
                BeepPermissionState.AllAllowedPermission
            }
            permissions.all { activity.checkPermission(it) } -> {
                BeepPermissionState.PartiallyAllowedPermission
            }
            else -> {
                BeepPermissionState.NotAllowedPermission
            }
        }
}

fun Context.checkPermission(permission: String) =
    checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
