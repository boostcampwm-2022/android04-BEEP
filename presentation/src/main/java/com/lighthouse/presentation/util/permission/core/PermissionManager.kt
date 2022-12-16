package com.lighthouse.presentation.util.permission.core

import android.app.Activity
import android.content.pm.PackageManager
import kotlinx.coroutines.flow.MutableStateFlow

abstract class PermissionManager(
    private val activity: Activity
) {
    abstract val permissions: Array<String>

    open val backGroundPermission = emptyArray<String>()

    val permission
        get() = permissions.firstOrNull() ?: ""

    val permissionFlow by lazy {
        MutableStateFlow(isGrant)
    }

    val isGrant
        get() = permissions.all { permission ->
            checkPermission(permission)
        }

    val isAllGrant
        get() = (permissions + backGroundPermission).all { permission ->
            checkPermission(permission)
        }

    private fun checkPermission(permission: String) =
        activity.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
}
