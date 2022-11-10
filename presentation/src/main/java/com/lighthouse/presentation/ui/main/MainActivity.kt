package com.lighthouse.presentation.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.commit
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.lighthouse.presentation.R
import com.lighthouse.presentation.databinding.ActivityMainBinding
import com.lighthouse.presentation.ui.addgifticon.AddGifticonActivity
import com.lighthouse.presentation.ui.gifticonlist.GifticonListFragment
import com.lighthouse.presentation.ui.home.HomeFragment
import com.lighthouse.presentation.ui.main.event.MainDirections
import com.lighthouse.presentation.ui.setting.SettingFragment
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()

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
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.directionsFlow.collect { directions ->
                    navigate(directions)
                }
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
        val intent = Intent(this, AddGifticonActivity::class.java)
        startActivity(intent)
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
