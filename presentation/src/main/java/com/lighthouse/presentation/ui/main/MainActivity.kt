package com.lighthouse.presentation.ui.main

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
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
import com.lighthouse.presentation.ui.main.event.MainDirections
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

    private val gifticonListFragment by lazy { GifticonListFragment() }
    private val homeFragment by lazy { HomeFragment() }
    private val settingFragment by lazy { SettingFragment() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.apply {
            lifecycleOwner = this@MainActivity
            vm = viewModel
        }

        moveScreen(MainDirections.HOME)
        setUpDirections()
    }

    private fun setUpDirections() {
        repeatOnStarted {
            viewModel.directionsFlow.collect { directions ->
                navigate(directions)
            }
        }
    }

    private fun navigate(directions: MainDirections) {
        when (directions) {
            MainDirections.ADD_GIFTICON -> gotoAddGifticon()
            else -> moveScreen(directions)
        }
    }

    private fun gotoAddGifticon() {
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            val intent = Intent(this, AddGifticonActivity::class.java)
            startActivity(intent)
        } else {
            permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

    private fun moveScreen(directions: MainDirections) {
        val fragment = when (directions) {
            MainDirections.LIST -> gifticonListFragment
            MainDirections.HOME -> homeFragment
            MainDirections.SETTING -> settingFragment
            else -> null
        } ?: return
        supportFragmentManager.commit {
            if (fragment != gifticonListFragment && gifticonListFragment.isAdded) hide(gifticonListFragment)
            if (fragment != homeFragment && homeFragment.isAdded) hide(homeFragment)
            if (fragment != settingFragment && settingFragment.isAdded) hide(settingFragment)
            if (fragment.isAdded) {
                show(fragment)
            } else {
                add(R.id.fl_container, fragment)
            }
        }
    }
}
