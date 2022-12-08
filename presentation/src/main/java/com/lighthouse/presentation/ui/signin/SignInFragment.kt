package com.lighthouse.presentation.ui.signin

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.lighthouse.presentation.R
import com.lighthouse.presentation.databinding.FragmentSignInBinding
import com.lighthouse.presentation.extension.repeatOnStarted
import com.lighthouse.presentation.extra.Extras
import com.lighthouse.presentation.ui.common.dialog.ProgressDialog
import com.lighthouse.presentation.ui.common.viewBindings
import com.lighthouse.presentation.ui.main.MainActivity
import com.lighthouse.presentation.ui.security.SecurityActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first

@AndroidEntryPoint
class SignInFragment : Fragment(R.layout.fragment_sign_in) {

    private val binding: FragmentSignInBinding by viewBindings()
    private val viewModel: SignInViewModel by viewModels()

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        checkAutoLogin()

        val animatedVectorDrawable = binding.ivLogo.drawable as AnimatedVectorDrawable
        animatedVectorDrawable.start()

        binding.tvGuestSignin.setOnClickListener {
            guestSignIn()
        }
    }

    private fun checkAutoLogin() {
        repeatOnStarted {
            if (viewModel.isGuestStored.first()) {
                if (viewModel.isGuest.first()) {
                    gotoMain()
                }
            }
        }

        if (auth.currentUser == null) {
            initGoogleLogin()
        } else {
            gotoMain()
        }
    }

    private fun initGoogleLogin() {
        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestIdToken(getString(R.string.default_web_client_id))
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireContext(), googleSignInOptions)

        binding.btnGoogleLogin.setOnClickListener {
            activityLauncher.launch(googleSignInClient.signInIntent)
            progressDialog.show(parentFragmentManager, "progress")
        }
    }

    private fun signInWithGoogle(account: GoogleSignInAccount) {
        account.email?.let { email ->
            progressDialog.dismiss()
            auth.fetchSignInMethodsForEmail(email).addOnCompleteListener { task ->
                val isInitial = task.result.signInMethods?.size == 0
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                auth.signInWithCredential(credential).addOnCompleteListener { signInTask ->
                    if (signInTask.isSuccessful) {
                        Snackbar.make(requireView(), getString(R.string.signin_success), Snackbar.LENGTH_SHORT).show()
                        viewModel.saveGuestOption(false)
                        if (isInitial) {
                            gotoSecurity()
                        } else {
                            gotoMain()
                        }
                    } else {
                        Snackbar.make(requireView(), getString(R.string.signin_fail), Snackbar.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun gotoMain() {
        val intent = Intent(requireContext(), MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun gotoSecurity() {
        val intent = Intent(requireContext(), SecurityActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra(Extras.KEY_PIN_REVISE, false)
        }
        startActivity(intent)
    }

    private fun guestSignIn() {
        viewModel.saveGuestOption(true)
        gotoSecurity()
    }
}
