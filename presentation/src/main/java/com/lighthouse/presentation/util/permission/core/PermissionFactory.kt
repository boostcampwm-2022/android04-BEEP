package com.lighthouse.presentation.util.permission.core

import android.content.Context
import com.lighthouse.presentation.util.permission.LocationPermissionManager
import com.lighthouse.presentation.util.permission.StoragePermissionManager

class PermissionFactory {
    fun <PM : PermissionManager> create(context: Context, managerClass: Class<PM>): PM {
        return when (managerClass) {
            StoragePermissionManager::class.java -> StoragePermissionManager(context)
            LocationPermissionManager::class.java -> LocationPermissionManager(context)
            else -> throw IllegalArgumentException("존재하지 않는 Permission Manager입니다.")
        } as PM
    }
}
