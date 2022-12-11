package com.lighthouse.presentation.ui.setting

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
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
import com.lighthouse.presentation.background.BeepWorkManager
import com.lighthouse.presentation.databinding.FragmentSettingMainBinding
import com.lighthouse.presentation.ui.common.dialog.ProgressDialog
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
    private val progressDialog by lazy { ProgressDialog() }

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

    private val locationLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            locationPermissionCheck()
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.vm = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        binding.tvUsedGifticon.setOnClickListener { gotoUsedGifticon() }
        binding.tvSecurity.setOnClickListener { authenticate() }
        binding.tvSignOut.setOnClickListener { signOut() }
        binding.tvWithdrawal.setOnClickListener { withdrawal() }
        binding.tvLocation.setOnClickListener { gotoPermissionSetting() }
        binding.smNotification.setOnCheckedChangeListener { _, isChecked ->
            val workManager = BeepWorkManager(requireContext())
            viewModel.saveNotificationOption(isChecked)
            when (isChecked) {
                true -> workManager.notificationEnqueue()
                false -> workManager.notificationCancel()
            }
        }

        if (viewModel.userPreferenceState.value.guest) {
            initGoogleLogin()
        }

        locationPermissionCheck()
    }

    private fun gotoSecuritySetting() {
        activityViewModel.gotoMenuItem(-1)
        parentFragmentManager.commit {
            add(R.id.fcv_setting, settingSecurityFragment)
        }
    }

    private fun gotoUsedGifticon() {
        activityViewModel.gotoMenuItem(-1)
        parentFragmentManager.commit {
            add(R.id.fcv_setting, UsedGifticonFragment())
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

    private fun withdrawal() {
        Firebase.auth.currentUser?.delete()
        viewModel.removeUserData()
        signOut()
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
        progressDialog.show(childFragmentManager, "progress")
        account.email?.let { email ->
            auth.fetchSignInMethodsForEmail(email).addOnCompleteListener { task ->
                progressDialog.dismiss()
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

    override fun onAuthError(stringId: Int?) {
        stringId?.let {
            Snackbar.make(requireView(), getString(it), Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun locationPermissionCheck() {
        for (permission in PERMISSIONS) {
            val result: Int = ContextCompat.checkSelfPermission(requireContext(), permission)
            if (PackageManager.PERMISSION_GRANTED != result) {
                binding.tvLocationOption.text = getString(R.string.location_not_allowed)
                return
            }
        }
        binding.tvLocationOption.text = getString(R.string.location_allowed)
    }

    private fun gotoPermissionSetting() {
        val intent = Intent().apply {
            action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            data = Uri.fromParts("package", requireActivity().packageName, null)
        }
        locationLauncher.launch(intent)
    }

    companion object {
        private val PERMISSIONS =
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
    }
}
