package com.lighthouse.presentation.ui.security

import android.Manifest
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.lighthouse.presentation.R
import com.lighthouse.presentation.ui.security.event.SecurityDirections

class CheckLocationFragment : Fragment(R.layout.fragment_check_location) {

    private val activityViewModel: SecurityViewModel by activityViewModels()

    private val contract = ActivityResultContracts.RequestMultiplePermissions()
    private val locationPermissionLauncher =
        registerForActivityResult(contract) {
            activityViewModel.gotoOtherScreen(SecurityDirections.MAIN)
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        locationPermissionLauncher.launch(PERMISSIONS)
    }

    companion object {
        private val PERMISSIONS =
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
    }
}
