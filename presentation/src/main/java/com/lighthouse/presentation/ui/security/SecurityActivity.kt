package com.lighthouse.presentation.ui.security

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.lighthouse.presentation.R

class SecurityActivity : AppCompatActivity() {

    private val fingerprintFragment by lazy { FingerprintFragment() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_security)

        supportFragmentManager.commit {
            add(R.id.fcv_security, fingerprintFragment)
        }
    }
}
