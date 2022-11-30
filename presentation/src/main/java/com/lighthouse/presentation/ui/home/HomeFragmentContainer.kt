package com.lighthouse.presentation.ui.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import com.lighthouse.presentation.R
import com.lighthouse.presentation.databinding.FragmentHomeContainerBinding
import com.lighthouse.presentation.extension.repeatOnStarted
import com.lighthouse.presentation.ui.common.viewBindings
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class HomeFragmentContainer : Fragment(R.layout.fragment_home_container) {

    private val binding: FragmentHomeContainerBinding by viewBindings()
    private val homeViewModel: HomeViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.lifecycleOwner = viewLifecycleOwner
        repeatOnStarted {
            homeViewModel.homeEvent.collectLatest { event ->
                delay(100)
                when (event) {
                    HomeEvent.NavigateDataNotExists -> childFragmentManager.commit {
                        replace(R.id.fcv_home, HomeEmptyFragment())
                    }
                    HomeEvent.NavigateDataExists -> childFragmentManager.commit {
                        replace(R.id.fcv_home, HomeFragment())
                    }
                }
            }
        }
    }
}
