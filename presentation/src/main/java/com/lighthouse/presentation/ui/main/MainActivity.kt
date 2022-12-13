package com.lighthouse.presentation.ui.main

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
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
import com.lighthouse.presentation.util.permission.StoragePermissionManager
import com.lighthouse.presentation.util.permission.core.permissions
import com.lighthouse.presentation.util.resource.UIText
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    private val storagePermission: StoragePermissionManager by permissions()

    val callback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            when (supportFragmentManager.fragments.first { it.isVisible }) {
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

    private var currentFragment: Fragment = homeFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.apply {
            lifecycleOwner = this@MainActivity
            vm = viewModel
        }

        onBackPressedDispatcher.addCallback(this, callback)

        supportFragmentManager.commit {
            add(R.id.fl_container, homeFragment, homeFragment.javaClass.name)
        }

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
                when (page) {
                    MainPage.List -> {
                        supportFragmentManager.commit {
                            setCustomAnimations(R.anim.anim_slide_in_left, R.anim.anim_slide_out_right)
                            hide(currentFragment)
                            if (gifticonListFragment.isAdded) {
                                show(gifticonListFragment)
                            } else {
                                add(R.id.fl_container, gifticonListFragment, gifticonListFragment.javaClass.name)
                            }
                            currentFragment = gifticonListFragment
                        }
                    }
                    MainPage.Setting -> {
                        supportFragmentManager.commit {
                            setCustomAnimations(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left)
                            hide(currentFragment)
                            if (settingFragment.isAdded) {
                                show(settingFragment)
                            } else {
                                add(R.id.fl_container, settingFragment, settingFragment.javaClass.name)
                            }
                            currentFragment = settingFragment
                        }
                    }
                    MainPage.Home -> {
                        supportFragmentManager.commit {
                            when (currentFragment) {
                                is GifticonListFragment -> {
                                    setCustomAnimations(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left)
                                }
                                is SettingFragment -> {
                                    setCustomAnimations(R.anim.anim_slide_in_left, R.anim.anim_slide_out_right)
                                }
                            }
                            hide(currentFragment)
                            if (homeFragment.isAdded) {
                                show(homeFragment)
                            } else {
                                add(R.id.fl_container, homeFragment, homeFragment.javaClass.name)
                            }
                            currentFragment = homeFragment
                        }
                    }
                    else -> {}
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

    private fun gotoAddGifticon() {
        if (storagePermission.isGrant) {
            val intent = Intent(this, AddGifticonActivity::class.java)
            addGifticon.launch(intent)
        } else {
            storagePermissionLauncher.launch(storagePermission.permission)
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
