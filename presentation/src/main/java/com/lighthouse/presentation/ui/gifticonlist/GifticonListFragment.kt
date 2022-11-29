package com.lighthouse.presentation.ui.gifticonlist

import android.os.Bundle
import android.view.View
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import com.google.accompanist.appcompattheme.AppCompatTheme
import com.lighthouse.presentation.R
import com.lighthouse.presentation.databinding.FragmentGifticonListBinding
import com.lighthouse.presentation.ui.common.viewBindings
import com.lighthouse.presentation.ui.gifticonlist.component.GifticonListScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GifticonListFragment : Fragment(R.layout.fragment_gifticon_list) {

    private val binding: FragmentGifticonListBinding by viewBindings()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.cvContainer.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                AppCompatTheme {
                    GifticonListScreen()
                }
            }
        }
    }
}
