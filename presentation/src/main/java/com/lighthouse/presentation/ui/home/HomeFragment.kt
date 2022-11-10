package com.lighthouse.presentation.ui.home

import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.lighthouse.presentation.R
import com.lighthouse.presentation.databinding.FragmentHomeBinding
import com.lighthouse.presentation.ui.common.viewBindings
import com.lighthouse.presentation.ui.main.MainViewModel

class HomeFragment : Fragment(R.layout.fragment_home) {

    private val binding by viewBindings(FragmentHomeBinding::bind)

    private val mainViewModel: MainViewModel by activityViewModels()
}
