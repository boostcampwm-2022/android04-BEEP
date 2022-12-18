package com.lighthouse.presentation.util.permission.core

sealed class BeepPermissionState {
    object NotAllowedPermission : BeepPermissionState()
    object AllAllowedPermission : BeepPermissionState()
    object PartiallyAllowedPermission : BeepPermissionState()
}
