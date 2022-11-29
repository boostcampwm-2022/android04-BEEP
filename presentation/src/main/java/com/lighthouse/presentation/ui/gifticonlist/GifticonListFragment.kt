package com.lighthouse.presentation.ui.gifticonlist

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.lighthouse.domain.usecase.SaveGifticonsUseCase
import com.lighthouse.presentation.R
import com.lighthouse.presentation.databinding.FragmentGifticonListBinding
import com.lighthouse.presentation.extra.Extras.KEY_GIFTICON_ID
import com.lighthouse.presentation.ui.common.viewBindings
import com.lighthouse.presentation.ui.detailgifticon.GifticonDetailActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class GifticonListFragment : Fragment(R.layout.fragment_gifticon_list) {

    private val binding: FragmentGifticonListBinding by viewBindings()

    @Inject
    lateinit var saveGifticonsUseCase: SaveGifticonsUseCase

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // TODO (테스트 용 코드 제거)
        binding.btnAddSampleData.setOnClickListener {
        }
        // TODO (테스트 용 코드 제거)
        binding.btnTest.setOnClickListener {
            startActivity(
                Intent(requireContext(), GifticonDetailActivity::class.java).apply {
                    val key = when (binding.rgTestOption.checkedRadioButtonId) {
                        R.id.rb1 -> "sample1"
                        R.id.rb2 -> "sample2"
                        R.id.rb3 -> "sample3"
                        else -> "sample4"
                    }
                    putExtra(KEY_GIFTICON_ID, key)
                }
            )
        }
    }
}
