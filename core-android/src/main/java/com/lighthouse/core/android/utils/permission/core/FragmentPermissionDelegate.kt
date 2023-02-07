package com.lighthouse.core.android.utils.permission.core

import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import kotlin.reflect.KProperty

class FragmentPermissionDelegate<PM : PermissionManager>(
    lifecycleOwner: LifecycleOwner,
    private val fragment: Fragment,
    private val managerClass: Class<PM>
) : PermissionDelegate<PM>(lifecycleOwner) {

    override fun getValue(thisRef: LifecycleOwner, property: KProperty<*>): PM {
        manager?.let {
            return it
        }

        return PermissionFactory().create(fragment.requireActivity(), managerClass).also {
            manager = it
        }
    }
}

inline fun <reified PM : PermissionManager> Fragment.permissions() =
    FragmentPermissionDelegate(this, this, PM::class.java)
