package com.lighthouse.core.android.utils.permission.core

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import kotlin.properties.ReadOnlyProperty

abstract class PermissionDelegate<PM : PermissionManager>(
    lifecycleOwner: LifecycleOwner
) : ReadOnlyProperty<LifecycleOwner, PermissionManager> {

    protected var manager: PM? = null

    init {
        lifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onStart(owner: LifecycleOwner) {
                val manager = manager
                if (manager != null) {
                    manager.permissionFlow.value = manager.isGrant
                }
            }

            override fun onDestroy(owner: LifecycleOwner) {
                lifecycleOwner.lifecycle.removeObserver(this)
            }
        })
    }
}
