package com.lighthouse.presentation.ui.signin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.lighthouse.presentation.R
import com.lighthouse.presentation.databinding.FragmentSignInBinding
import com.lighthouse.presentation.ui.common.viewBindings

class SignInFragment : Fragment() {

    private val binding by viewBindings(FragmentSignInBinding::bind)
    private val viewModel: SignInViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_sign_in, container, false)
    }

}
