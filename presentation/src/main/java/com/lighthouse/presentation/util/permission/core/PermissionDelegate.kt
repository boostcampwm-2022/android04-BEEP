package com.lighthouse.presentation.util.permission.core

import android.content.Context
import androidx.core.app.ComponentActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class PermissionDelegate<PM : PermissionManager>(
    lifecycleOwner: LifecycleOwner,
    private val context: Context,
    private val managerClass: Class<PM>
) : ReadOnlyProperty<LifecycleOwner, PermissionManager> {

    private var manager: PM? = null

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

    override fun getValue(thisRef: LifecycleOwner, property: KProperty<*>): PM {
        manager?.let {
            return it
        }

        return PermissionFactory().create(context.applicationContext, managerClass).also {
            manager = it
        }
    }
}

inline fun <reified PM : PermissionManager> ComponentActivity.permissions() =
    PermissionDelegate(this, this, PM::class.java)

inline fun <reified PM : PermissionManager> Fragment.permissions() =
    PermissionDelegate(this, requireContext(), PM::class.java)
