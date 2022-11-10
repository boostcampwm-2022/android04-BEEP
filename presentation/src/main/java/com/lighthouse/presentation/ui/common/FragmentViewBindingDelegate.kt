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
    private val viewBindingFactory: (View) -> VB
) : ReadOnlyProperty<Fragment, VB> {

    private var binding: VB? = null

    init {
        fragment.lifecycle.addObserver(object : DefaultLifecycleObserver {
            private val viewLifecycleOwnerObserver = Observer<LifecycleOwner?> {
                val viewLifecycleOwner = it ?: return@Observer
                viewLifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
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
            }
        })
    }

    override fun getValue(thisRef: Fragment, property: KProperty<*>): VB {
        val binding = binding
        if (binding != null) {
            return binding
        }

        val lifecycle = fragment.viewLifecycleOwner.lifecycle
        if (!lifecycle.currentState.isAtLeast(Lifecycle.State.INITIALIZED)) {
            throw IllegalStateException("Fragment 의 View가 제거 됬을땐 Binding을 가져오면 안된다.")
        }

        return viewBindingFactory(thisRef.requireView()).also {
            this.binding = it
        }
    }
}

fun <VB : ViewBinding> Fragment.viewBindings(viewBindingFactory: (View) -> VB) =
    FragmentViewBindingDelegate(this, viewBindingFactory)
