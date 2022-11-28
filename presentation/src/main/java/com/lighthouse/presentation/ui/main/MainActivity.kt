package com.lighthouse.presentation.ui.main

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.commit
import com.lighthouse.presentation.R
import com.lighthouse.presentation.databinding.ActivityMainBinding
import com.lighthouse.presentation.extension.repeatOnStarted
import com.lighthouse.presentation.ui.addgifticon.AddGifticonActivity
import com.lighthouse.presentation.ui.gifticonlist.GifticonListFragment
import com.lighthouse.presentation.ui.home.HomeFragment
import com.lighthouse.presentation.ui.map.MapActivity
import com.lighthouse.presentation.ui.setting.SettingFragment
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
                    MainEvent.NavigateAddGifticon -> gotoAddGifticon()
                    MainEvent.NavigateMap -> gotoMap()
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
                            add(R.id.fl_container, fg)
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
                if (show) {
                    binding.bnv.visibility = View.VISIBLE
                } else {
                    binding.bnv.visibility = View.GONE
                }
            }
        }
    }

    private fun gotoMap() {
        val intent = Intent(this, MapActivity::class.java)
        startActivity(intent)
    }

    private fun gotoAddGifticon() {
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            val intent = Intent(this, AddGifticonActivity::class.java)
            startActivity(intent)
        } else {
            permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }
}
