package com.lighthouse.presentation.ui.common

import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.viewbinding.ViewBinding
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class FragmentViewBindingDelegate<VB : ViewBinding>(
    private val fragment: Fragment,
    bindingClass: Class<VB>
) : ReadOnlyProperty<Fragment, VB> {

    private var binding: VB? = null

    private val bindMethod = bindingClass.getMethod("bind", View::class.java)

    private val fragmentObserver = object : DefaultLifecycleObserver {
        private val viewLifecycleOwnerObserver = Observer<LifecycleOwner?> { viewLifecycleOwner ->
            viewLifecycleOwner?.lifecycle?.addObserver(object : DefaultLifecycleObserver {
                override fun onDestroy(owner: LifecycleOwner) {
                    binding = null
                }
            })
        }

        override fun onCreate(owner: LifecycleOwner) {
            fragment.viewLifecycleOwnerLiveData.observeForever(viewLifecycleOwnerObserver)
        }

        override fun onDestroy(owner: LifecycleOwner) {
            fragment.viewLifecycleOwnerLiveData.removeObserver(viewLifecycleOwnerObserver)
            fragment.lifecycle.removeObserver(this)
        }
    }

    override fun getValue(thisRef: Fragment, property: KProperty<*>): VB {
        binding?.let {
            return it
        }

        fragment.lifecycle.addObserver(fragmentObserver)

        val lifecycle = fragment.viewLifecycleOwner.lifecycle
        if (!lifecycle.currentState.isAtLeast(Lifecycle.State.INITIALIZED)) {
            throw IllegalStateException("Fragment 의 View가 제거 됬을땐 Binding을 가져오면 안된다.")
        }

        val value = bindMethod.invoke(null, thisRef.requireView()) as VB
        return value.also {
            binding = it
        }
    }
}

inline fun <reified VB : ViewBinding> Fragment.viewBindings() = FragmentViewBindingDelegate(this, VB::class.java)
