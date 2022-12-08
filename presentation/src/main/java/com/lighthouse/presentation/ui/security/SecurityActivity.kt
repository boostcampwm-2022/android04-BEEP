package com.lighthouse.presentation.ui.security

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.lighthouse.presentation.R
import com.lighthouse.presentation.extension.repeatOnStarted
import com.lighthouse.presentation.ui.main.MainActivity
import com.lighthouse.presentation.ui.security.event.SecurityDirections
import com.lighthouse.presentation.ui.security.fingerprint.FingerprintFragment
import com.lighthouse.presentation.ui.security.pin.PinFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SecurityActivity : AppCompatActivity() {

    private val viewModel: SecurityViewModel by viewModels()
    private val fingerprintFragment by lazy { FingerprintFragment() }
    private val pinFragment by lazy { PinFragment() }
    private val checkLocationFragment by lazy { CheckLocationFragment() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_security)

        moveScreen(SecurityDirections.PIN)
        repeatOnStarted {
            viewModel.directionsFlow.collect { directions ->
                navigate(directions)
            }
        }
    }

    private fun navigate(directions: SecurityDirections) {
        when (directions) {
            SecurityDirections.MAIN -> gotoMain()
            else -> moveScreen(directions)
        }
    }

    private fun gotoMain() {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
    }

    private fun moveScreen(directions: SecurityDirections) {
        val fragment = when (directions) {
            SecurityDirections.FINGERPRINT -> fingerprintFragment
            SecurityDirections.PIN -> pinFragment
            SecurityDirections.LOCATION -> checkLocationFragment
            else -> return
        }
        supportFragmentManager.commit {
            if (fragment != fingerprintFragment && fingerprintFragment.isAdded) hide(fingerprintFragment)
            if (fragment != pinFragment && pinFragment.isAdded) hide(pinFragment)
            if (fragment != checkLocationFragment && checkLocationFragment.isAdded) hide(checkLocationFragment)
            if (fragment.isAdded) {
                show(fragment)
            } else {
                add(R.id.fcv_security, fragment)
            }
        }
    }
}
