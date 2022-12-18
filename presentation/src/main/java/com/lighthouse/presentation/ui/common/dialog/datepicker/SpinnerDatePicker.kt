package com.lighthouse.presentation.ui.common.dialog.datepicker

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.lighthouse.presentation.R
import com.lighthouse.presentation.databinding.DialogSpinnerDatePickerBinding
import com.lighthouse.presentation.ui.common.viewBindings
import java.util.Calendar
import java.util.Date

class SpinnerDatePicker : DialogFragment(R.layout.dialog_spinner_date_picker) {

    private val binding: DialogSpinnerDatePickerBinding by viewBindings()

    private val viewModel: SpinnerDatePickerViewModel by viewModels()

    private var initDate = Date()
    fun setDate(date: Date) {
        initDate = date
    }

    fun setDate(year: Int, month: Int, dayOfMonth: Int) {
        val date = Calendar.getInstance().let {
            it.set(year, month, dayOfMonth)
            it.time
        }
        setDate(date)
    }

    private var onDatePickListener: OnDatePickListener? = null
    fun setOnDatePickListener(listener: ((Int, Int, Int) -> Unit)?) {
        onDatePickListener = listener?.let {
            object : OnDatePickListener {
                override fun onDatePick(year: Int, month: Int, date: Int) {
                    it(year, month, date)
                }
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            window?.apply {
                setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            vm = viewModel
        }
        viewModel.setDate(initDate)
        binding.root.setOnClickListener {
            dismiss()
        }

        binding.btnOk.setOnClickListener {
            onDatePickListener?.onDatePick(viewModel.year, viewModel.month, viewModel.dayOfMonth)
            dismiss()
        }
    }

    override fun onResume() {
        super.onResume()

        dialog?.window?.apply {
            attributes = attributes.apply {
                width = WindowManager.LayoutParams.MATCH_PARENT
                height = WindowManager.LayoutParams.MATCH_PARENT
            }
        }

        binding.apply {
            npYear.value = viewModel.year
            npMonth.value = viewModel.month
            npDayOfMonth.value = viewModel.dayOfMonth
        }
    }
}
