package com.lighthouse.presentation.ui.history

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.lighthouse.presentation.databinding.DialogHistoryBinding
import com.lighthouse.presentation.extension.screenHeight
import com.lighthouse.presentation.model.HistoryUiModel
import com.lighthouse.presentation.ui.history.adapter.gifticondetail.HistoryAdapter
import timber.log.Timber

class HistoryBottomSheet : BottomSheetDialogFragment() {

    private lateinit var binding: DialogHistoryBinding
    private val historyAdapter = HistoryAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DialogHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
            ?.backgroundTintList = ColorStateList.valueOf(Color.TRANSPARENT)
        binding.clContainer.apply {
            layoutParams = layoutParams.apply {
                height = (screenHeight * 0.9).toInt()
            }
        }
        binding.rvHistory.adapter = historyAdapter
        binding.rvHistory.addItemDecoration(HistoryItemDecoration(binding.rvHistory.context))
    }

    fun submitList(history: List<HistoryUiModel>) {
        Timber.d("history: $history")
        historyAdapter.submitList(history)
    }

    companion object {
        const val TAG = "HistoryBottomSheetDialog"
    }
}
