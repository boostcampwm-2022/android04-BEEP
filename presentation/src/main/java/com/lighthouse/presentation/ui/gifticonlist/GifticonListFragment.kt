package com.lighthouse.presentation.ui.gifticonlist

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.lighthouse.presentation.R
import com.lighthouse.presentation.databinding.FragmentGifticonListBinding
import com.lighthouse.presentation.extra.Extras.KEY_GIFTICON_ID
import com.lighthouse.presentation.ui.common.viewBindings
import com.lighthouse.presentation.ui.detailgifticon.GifticonDetailActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GifticonListFragment : Fragment(R.layout.fragment_gifticon_list) {

    private val binding by viewBindings(FragmentGifticonListBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // TODO (테스트 용 코드 제거)
        binding.btnTest.setOnClickListener {
            startActivity(
                Intent(requireContext(), GifticonDetailActivity::class.java).apply {
                    putExtra(KEY_GIFTICON_ID, "테스트 중입니다아")
                }
            )
        }
    }
}
