package com.lighthouse.presentation.ui.signin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.lighthouse.presentation.R
import com.lighthouse.presentation.databinding.ActivitySignInBinding

class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding

    private val signInFragment by lazy { SignInFragment() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_in)
        supportFragmentManager.commit {
            add(R.id.fl_signin, signInFragment)
        }
    }
}
