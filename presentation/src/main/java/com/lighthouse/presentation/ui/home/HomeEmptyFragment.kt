package com.lighthouse.presentation.ui.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.lighthouse.presentation.R
import com.lighthouse.presentation.databinding.FragmentHomeEmptyBinding
import com.lighthouse.presentation.ui.common.viewBindings
import com.lighthouse.presentation.ui.main.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class HomeEmptyFragment : Fragment(R.layout.fragment_home_empty) {

    private val binding: FragmentHomeEmptyBinding by viewBindings()
    private val mainViewModel: MainViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.vm = mainViewModel
        Timber.tag("TAG").d("${javaClass.simpleName} 데이터가 존재하지 않아서 HomeDataNotFragment 이동")
    }
}
