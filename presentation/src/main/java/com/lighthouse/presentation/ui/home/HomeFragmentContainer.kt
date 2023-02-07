package com.lighthouse.presentation.ui.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import com.lighthouse.presentation.R
import com.lighthouse.presentation.databinding.FragmentHomeContainerBinding
import com.lighthouse.presentation.extension.repeatOnStarted
import com.lighthouse.presentation.ui.common.viewBindings
import com.lighthouse.presentation.ui.main.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class HomeFragmentContainer : Fragment(R.layout.fragment_home_container) {

    private val binding: FragmentHomeContainerBinding by viewBindings()
    private val mainViewModel: MainViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.lifecycleOwner = viewLifecycleOwner
        viewLifecycleOwner.repeatOnStarted {
            mainViewModel.hasVariableGifticon.collectLatest { hasGifticon ->
                when (hasGifticon) {
                    true -> childFragmentManager.commit { replace(R.id.fcv_home, HomeFragment()) }
                    false -> childFragmentManager.commit {
                        replace(
                            R.id.fcv_home,
                            HomeEmptyFragment()
                        )
                    }
                }
            }
        }
    }
}
