package com.lighthouse.presentation.ui.main

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.commit
import com.google.android.material.snackbar.Snackbar
import com.lighthouse.domain.model.Gifticon
import com.lighthouse.presentation.R
import com.lighthouse.presentation.databinding.ActivityMainBinding
import com.lighthouse.presentation.extension.repeatOnStarted
import com.lighthouse.presentation.extra.Extras
import com.lighthouse.presentation.model.BrandPlaceInfoUiModel
import com.lighthouse.presentation.ui.addgifticon.AddGifticonActivity
import com.lighthouse.presentation.ui.common.dialog.ConfirmationDialog
import com.lighthouse.presentation.ui.gifticonlist.GifticonListFragment
import com.lighthouse.presentation.ui.home.HomeFragment
import com.lighthouse.presentation.ui.map.MapActivity
import com.lighthouse.presentation.ui.setting.SettingFragment
import com.lighthouse.presentation.util.resource.UIText
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                gotoAddGifticon()
            }
        }

    private val gifticonListFragment by lazy {
        supportFragmentManager.findFragmentByTag(GifticonListFragment::class.java.name) ?: GifticonListFragment()
    }
    private val homeFragment by lazy {
        supportFragmentManager.findFragmentByTag(HomeFragment::class.java.name) ?: HomeFragment()
    }
    private val settingFragment by lazy {
        supportFragmentManager.findFragmentByTag(SettingFragment::class.java.name) ?: SettingFragment()
    }

    private val addGifticon = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            showSnackBar(UIText.StringResource(R.string.main_registration_completed))
        }
    }

    private val locationPermissionDialog by lazy {
        val title = getString(R.string.confirmation_title)
        val message = getString(R.string.confirmation_location_message)
        ConfirmationDialog().apply {
            setTitle(title)
            setMessage(message)
            setOnOkClickListener {
                val intent = Intent()
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                intent.data = Uri.fromParts("package", packageName, null)
                startActivity(intent)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.apply {
            lifecycleOwner = this@MainActivity
            vm = viewModel
        }

        collectEvent()
        collectPage()
        collectFab()
        collectBnv()
    }

    private fun collectEvent() {
        repeatOnStarted {
            viewModel.eventFlow.collect { directions ->
                when (directions) {
                    is MainEvent.NavigateAddGifticon -> gotoAddGifticon()
                    is MainEvent.NavigateMap -> gotoMap(directions.gifticons, directions.nearBrandsInfo)
                }
            }
        }
    }

    private fun collectPage() {
        repeatOnStarted {
            viewModel.pageFlow.collect { page ->
                val fragment = when (page) {
                    MainPage.List -> gifticonListFragment
                    MainPage.Home -> homeFragment
                    MainPage.Setting -> settingFragment
                    else -> null
                }
                fragment?.let { fg ->
                    supportFragmentManager.commit {
                        if (fg != gifticonListFragment && gifticonListFragment.isAdded) hide(gifticonListFragment)
                        if (fg != homeFragment && homeFragment.isAdded) hide(homeFragment)
                        if (fg != settingFragment && settingFragment.isAdded) hide(settingFragment)
                        if (fg.isAdded) {
                            show(fragment)
                        } else {
                            add(R.id.fl_container, fg, fg.javaClass.name)
                        }
                    }
                }
            }
        }
    }

    private fun collectFab() {
        repeatOnStarted {
            viewModel.fabFlow.collect { show ->
                if (show) {
                    binding.fabAddGifticon.show()
                } else {
                    binding.fabAddGifticon.hide()
                }
            }
        }
    }

    private fun collectBnv() {
        repeatOnStarted {
            viewModel.bnvFlow.collect { show ->
                binding.bnv.isVisible = show
            }
        }
    }

    private fun gotoMap(gifticons: List<Gifticon>, nearBrandsInfo: List<BrandPlaceInfoUiModel>) {
        if (isPermissionGranted()) {
            startActivity(
                Intent(this, MapActivity::class.java).apply {
                    putExtra(Extras.KEY_NEAR_BRANDS, ArrayList(nearBrandsInfo))
                    putExtra(Extras.KEY_NEAR_GIFTICONS, ArrayList(gifticons))
                }
            )
        } else {
            requestPermissions(PERMISSIONS, REQUEST_PERMISSIONS_REQUEST_CODE)
        }
    }

    private fun isPermissionGranted(): Boolean {
        for (permission in PERMISSIONS) {
            if (permission == Manifest.permission.ACCESS_BACKGROUND_LOCATION && Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                continue
            }
            val result: Int = ContextCompat.checkSelfPermission(this, permission)
            if (PackageManager.PERMISSION_GRANTED != result) {
                return false
            }
        }
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.isEmpty()) return // 사용자 상호작용이 없이 종료될때

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startActivity(Intent(this, MapActivity::class.java))
            } else {
                locationPermissionDialog.show(supportFragmentManager, ConfirmationDialog::class.java.name)
            }
        }
    }

    private fun gotoAddGifticon() {
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            val intent = Intent(this, AddGifticonActivity::class.java)
            addGifticon.launch(intent)
        } else {
            permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

    private fun showSnackBar(uiText: UIText) {
        Snackbar.make(binding.root, uiText.asString(applicationContext), Snackbar.LENGTH_SHORT).show()
    }

    companion object {
        private const val REQUEST_PERMISSIONS_REQUEST_CODE = 1981
        private val PERMISSIONS =
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
    }
}
