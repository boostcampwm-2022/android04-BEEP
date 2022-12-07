package com.lighthouse.presentation.ui.setting

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.lighthouse.presentation.R
import com.lighthouse.presentation.databinding.FragmentSettingMainBinding
import com.lighthouse.presentation.ui.common.viewBindings
import com.lighthouse.presentation.ui.main.MainViewModel
import com.lighthouse.presentation.ui.security.AuthCallback
import com.lighthouse.presentation.ui.security.AuthManager
import com.lighthouse.presentation.ui.signin.SignInActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingMainFragment : Fragment(R.layout.fragment_setting_main), AuthCallback {

    private val binding: FragmentSettingMainBinding by viewBindings()
    private val viewModel: SettingViewModel by activityViewModels()
    private val activityViewModel: MainViewModel by activityViewModels()

    private val settingSecurityFragment by lazy { SettingSecurityFragment() }

    private lateinit var googleSignInClient: GoogleSignInClient
    private val auth: FirebaseAuth = Firebase.auth
    private val activityLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)

                try {
                    signInWithGoogle(task.getResult(ApiException::class.java))
                } catch (e: ApiException) {
                    Snackbar.make(requireView(), getString(R.string.signin_google_fail), Snackbar.LENGTH_SHORT).show()
                }
            } else {
                Snackbar.make(requireView(), getString(R.string.signin_google_connect_fail), Snackbar.LENGTH_SHORT)
                    .show()
            }
        }

    @Inject
    lateinit var authManager: AuthManager
    private val biometricLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            when (result.resultCode) {
                Activity.RESULT_OK -> authenticate()
                else -> onAuthError()
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.vm = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        binding.tvSecurity.setOnClickListener { authenticate() }
        binding.tvSignOut.setOnClickListener { signOut() }

        if (viewModel.userPreferenceState.value.guest) {
            initGoogleLogin()
        }
    }

    private fun gotoSecuritySetting() {
        activityViewModel.gotoMenuItem(-1)
        parentFragmentManager.commit {
            add(R.id.fcv_setting, settingSecurityFragment)
        }
    }

    private fun authenticate() {
        authManager.auth(requireActivity(), biometricLauncher, this)
    }

    private fun signOut() {
        Firebase.auth.signOut()
        val intent = Intent(requireContext(), SignInActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun initGoogleLogin() {
        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestIdToken(getString(R.string.default_web_client_id))
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireContext(), googleSignInOptions)

        binding.tvSignIn.setOnClickListener {
            activityLauncher.launch(googleSignInClient.signInIntent)
        }
    }

    private fun signInWithGoogle(account: GoogleSignInAccount) {
        account.email?.let { email ->
            auth.fetchSignInMethodsForEmail(email).addOnCompleteListener { task ->
                val isInitial = task.result.signInMethods?.size == 0
                if (isInitial) {
                    val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                    auth.signInWithCredential(credential).addOnCompleteListener { signInTask ->
                        if (signInTask.isSuccessful) {
                            Snackbar.make(requireView(), getString(R.string.signin_success), Snackbar.LENGTH_SHORT).show()
                            auth.uid?.let { viewModel.moveGuestData(it) }
                        } else {
                            Snackbar.make(requireView(), getString(R.string.signin_fail), Snackbar.LENGTH_SHORT).show()
                        }
                    }
                    // TODO: 신규 google 로그인
                } else {
                    MaterialAlertDialogBuilder(requireContext())
                        .setMessage(R.string.setting_already_exist_email)
                        .show()
                }
            }
        }
    }

    override fun onAuthSuccess() {
        gotoSecuritySetting()
    }

    override fun onAuthCancel() {
    }

    override fun onAuthError(StringId: Int?) {
        StringId?.let {
            Snackbar.make(requireView(), getString(it), Snackbar.LENGTH_SHORT).show()
        }
    }
}
