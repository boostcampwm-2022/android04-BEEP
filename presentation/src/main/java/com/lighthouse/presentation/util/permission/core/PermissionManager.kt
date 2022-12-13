package com.lighthouse.presentation.util.permission.core

import android.content.Context
import android.content.pm.PackageManager
import kotlinx.coroutines.flow.MutableStateFlow

abstract class PermissionManager(
    private val context: Context
) {
    abstract val permissions: Array<String>

    val permission
        get() = permissions.firstOrNull() ?: ""

    val permissionFlow = MutableStateFlow<Boolean?>(null)

    val isGrant
        get() = permissions.all { permission ->
            context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
        }
}
