package com.lighthouse.presentation.ui.security

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.lighthouse.presentation.R
import com.lighthouse.presentation.ui.security.event.SecurityDirections
import kotlinx.coroutines.launch

class SecurityActivity : AppCompatActivity() {

    private val fingerprintFragment by lazy { FingerprintFragment() }
    private val pinFragment by lazy { PinFragment() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_security)

        moveScreen(SecurityDirections.FINGERPRINT)
    }

    private fun moveScreen(directions: SecurityDirections) {
        val fragment = when (directions) {
            SecurityDirections.FINGERPRINT -> fingerprintFragment
            SecurityDirections.PIN -> pinFragment
        }
        supportFragmentManager.commit {
            if (fragment != fingerprintFragment && fingerprintFragment.isAdded) hide(fingerprintFragment)
            if (fragment != pinFragment && pinFragment.isAdded) hide(pinFragment)
            if (fragment.isAdded) {
                show(fragment)
            } else {
                add(R.id.fcv_security, fragment)
            }
        }
    }
}
