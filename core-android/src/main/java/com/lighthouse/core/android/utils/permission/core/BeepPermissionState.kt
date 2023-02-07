package com.lighthouse.core.android.utils.permission.core

sealed class BeepPermissionState {
    object NotAllowedPermission : BeepPermissionState()
    object AllAllowedPermission : BeepPermissionState()
    object PartiallyAllowedPermission : BeepPermissionState()
}
