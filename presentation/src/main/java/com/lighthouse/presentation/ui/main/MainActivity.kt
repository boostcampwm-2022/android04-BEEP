package com.lighthouse.presentation.ui.main

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.commit
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.lighthouse.presentation.R
import com.lighthouse.presentation.databinding.ActivityMainBinding
import com.lighthouse.presentation.extension.repeatOnStarted
import com.lighthouse.presentation.extra.Extras
import com.lighthouse.presentation.ui.addgifticon.AddGifticonActivity
import com.lighthouse.presentation.ui.gifticonlist.GifticonListFragment
import com.lighthouse.presentation.ui.home.HomeFragmentContainer
import com.lighthouse.presentation.ui.map.MapActivity
import com.lighthouse.presentation.ui.security.SecurityActivity
import com.lighthouse.presentation.ui.setting.SettingFragment
import com.lighthouse.presentation.ui.setting.SettingSecurityFragment
import com.lighthouse.presentation.util.resource.UIText
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    private val callback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            when (supportFragmentManager.fragments.filter { it.isVisible }[0]) {
                is HomeFragmentContainer -> finish()
                is SettingSecurityFragment -> {} // SettingSecurityFragment 에서 관리하는 부분을 옮기려고 했는데 안 되네요...
                else -> {
                    binding.bnv.selectedItemId = R.id.menu_home
                    viewModel.gotoHome()
                }
            }
        }
    }

    private val storagePermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                gotoAddGifticon()
            }
        }

    private val gifticonListFragment by lazy {
        supportFragmentManager.findFragmentByTag(GifticonListFragment::class.java.name) ?: GifticonListFragment()
    }
    private val homeFragment by lazy {
        supportFragmentManager.findFragmentByTag(HomeFragmentContainer::class.java.name) ?: HomeFragmentContainer()
    }
    private val settingFragment by lazy {
        supportFragmentManager.findFragmentByTag(SettingFragment::class.java.name) ?: SettingFragment()
    }

    private val addGifticon = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            showSnackBar(UIText.StringResource(R.string.main_registration_completed))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.apply {
            lifecycleOwner = this@MainActivity
            vm = viewModel
        }

        onBackPressedDispatcher.addCallback(this, callback)

        checkUserPreferenceOption()
        collectEvent()
        collectPage()
        collectFab()
        collectBnv()
    }

    private fun checkUserPreferenceOption() {
        repeatOnStarted {
            if (viewModel.isSecurityOptionExist.first().not()) {
                MaterialAlertDialogBuilder(this)
                    .setTitle(getString(R.string.security_not_set))
                    .setMessage(getString(R.string.security_not_set_description))
                    .setNeutralButton(getString(R.string.all_not_use)) { dialog, which ->
                        viewModel.saveSecurityNotUse()
                    }
                    .setPositiveButton(getString(R.string.main_menu_setting)) { dialog, which ->
                        gotoSecurity()
                    }
                    .setCancelable(false)
                    .show()
            }

            if (viewModel.isNotificationOptionExist.first().not()) {
                viewModel.saveNotificationUse()
            }
        }
    }

    private fun collectEvent() {
        repeatOnStarted {
            viewModel.eventFlow.collect { directions ->
                when (directions) {
                    is MainEvent.NavigateAddGifticon -> gotoAddGifticon()
                    is MainEvent.NavigateMap -> gotoMap(directions.brand)
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

    private fun isStoragePermissionGrant(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            checkSelfPermission(Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED
        } else {
            checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun getStoragePermission(): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
    }

    private fun gotoAddGifticon() {
        if (isStoragePermissionGrant()) {
            val intent = Intent(this, AddGifticonActivity::class.java)
            addGifticon.launch(intent)
        } else {
            storagePermissionLauncher.launch(getStoragePermission())
        }
    }

    private fun gotoMap(brand: String) {
        val intent = Intent(this, MapActivity::class.java).apply {
            putExtra(Extras.KEY_WIDGET_BRAND, brand)
        }
        startActivity(intent)
    }

    private fun gotoSecurity() {
        val intent = Intent(this, SecurityActivity::class.java).apply {
            putExtra(Extras.KEY_PIN_REVISE, false)
        }
        startActivity(intent)
    }

    private fun showSnackBar(uiText: UIText) {
        Snackbar.make(binding.root, uiText.asString(applicationContext), Snackbar.LENGTH_SHORT).show()
    }
}
