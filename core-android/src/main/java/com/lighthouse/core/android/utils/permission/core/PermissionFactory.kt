package com.lighthouse.core.android.utils.permission.core

import android.app.Activity
import com.lighthouse.core.android.utils.permission.LocationPermissionManager
import com.lighthouse.core.android.utils.permission.StoragePermissionManager

@Suppress("UNCHECKED_CAST")
class PermissionFactory {
    fun <PM : PermissionManager> create(activity: Activity, managerClass: Class<PM>): PM {
        return when (managerClass) {
            StoragePermissionManager::class.java -> StoragePermissionManager(activity)
            LocationPermissionManager::class.java -> LocationPermissionManager(activity)
            else -> throw IllegalArgumentException("존재하지 않는 Permission Manager입니다.")
        } as PM
    }
}
