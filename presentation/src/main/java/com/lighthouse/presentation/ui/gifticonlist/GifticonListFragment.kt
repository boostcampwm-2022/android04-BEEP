package com.lighthouse.presentation.ui.gifticonlist

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.lighthouse.domain.model.Gifticon
import com.lighthouse.domain.usecase.SaveGifticonsUseCase
import com.lighthouse.presentation.R
import com.lighthouse.presentation.databinding.FragmentGifticonListBinding
import com.lighthouse.presentation.extra.Extras.KEY_GIFTICON_ID
import com.lighthouse.presentation.ui.common.viewBindings
import com.lighthouse.presentation.ui.detailgifticon.GifticonDetailActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Date
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
            lifecycleScope.launch {
                saveGifticonsUseCase(
                    listOf(
                        Gifticon(
                            id = "sample1",
                            userId = "mangbaam",
                            name = "별다방 아메리카노",
                            brand = "스타벅스",
                            expireAt = Date(),
                            barcode = "808346588450",
                            isCashCard = false,
                            balance = 0,
                            memo = "",
                            isUsed = false
                        ),
                        Gifticon(
                            id = "sample2",
                            userId = "mangbaam",
                            name = "5만원권",
                            brand = "GS25",
                            expireAt = Date(),
                            barcode = "808346588450",
                            isCashCard = true,
                            balance = 50000,
                            memo = "",
                            isUsed = false
                        ),
                        Gifticon(
                            id = "sample3",
                            userId = "mangbaam",
                            name = "어머니는 외계인",
                            brand = "베스킨라빈스31",
                            expireAt = Date(),
                            barcode = "808346588450",
                            isCashCard = false,
                            balance = 0,
                            memo = "",
                            isUsed = true
                        )
                    )
                )
            }
        }
        // TODO (테스트 용 코드 제거)
        binding.btnTest.setOnClickListener {
            startActivity(
                Intent(requireContext(), GifticonDetailActivity::class.java).apply {
                    putExtra(KEY_GIFTICON_ID, "sample1")
                }
            )
        }
    }
}
