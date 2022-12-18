package com.lighthouse.presentation.util.permission.core

import android.app.Activity
import androidx.core.app.ComponentActivity
import androidx.lifecycle.LifecycleOwner
import kotlin.reflect.KProperty

class ActivityPermissionDelegate<PM : PermissionManager>(
    lifecycleOwner: LifecycleOwner,
    private val activity: Activity,
    private val managerClass: Class<PM>
) : PermissionDelegate<PM>(lifecycleOwner) {

    override fun getValue(thisRef: LifecycleOwner, property: KProperty<*>): PM {
        manager?.let {
            return it
        }

        return PermissionFactory().create(activity, managerClass).also {
            manager = it
        }
    }
}

inline fun <reified PM : PermissionManager> ComponentActivity.permissions() =
    ActivityPermissionDelegate(this, this, PM::class.java)
