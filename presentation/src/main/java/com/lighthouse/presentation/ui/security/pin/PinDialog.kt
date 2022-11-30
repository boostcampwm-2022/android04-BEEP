package com.lighthouse.presentation.ui.security.pin

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.lighthouse.presentation.R
import com.lighthouse.presentation.databinding.FragmentPinBinding
import com.lighthouse.presentation.extension.repeatOnStarted
import com.lighthouse.presentation.extension.screenHeight
import com.lighthouse.presentation.ui.common.viewBindings
import com.lighthouse.presentation.ui.security.AuthCallback
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import timber.log.Timber

@AndroidEntryPoint
class PinDialog(private val authCallback: AuthCallback) : BottomSheetDialogFragment(R.layout.fragment_pin) {

    private val binding: FragmentPinBinding by viewBindings()
    private val viewModel: PinDialogViewModel by viewModels()

    private val shakeAnimation: Animation by lazy {
        AnimationUtils.loadAnimation(requireActivity(), R.anim.anim_shake)
    }

    private val fadeUpAnimation: Animation by lazy {
        AnimationUtils.loadAnimation(requireActivity(), R.anim.anim_fadein_up)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.tag("WHY").d("Dialog: onViewCreated")
        binding.vm = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        binding.btnPinPrev.visibility = View.INVISIBLE

        initBottomSheetDialog(view)
        repeatOnStarted {
            viewModel.pinMode.collect { mode ->
                when (mode) {
                    PinSettingType.CONFIRM -> binding.tvPinDescription.text = getString(R.string.pin_input_description)
                    PinSettingType.WRONG -> {
                        binding.tvPinDescription.text = getString(R.string.pin_wrong_description)
                        playWrongPinAnimation()
                    }
                    PinSettingType.COMPLETE -> {
                        binding.ivCheck.apply {
                            visibility = View.VISIBLE
                            startAnimation(fadeUpAnimation)
                        }
                        authCallback.onAuthSuccess()
                        delay(1000L)
                        dismiss()
                    }
                    else -> {}
                }
            }
        }
    }

    private fun initBottomSheetDialog(view: View) {
        val bottomSheetBehavior = BottomSheetBehavior.from(view.parent as View)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        binding.clPin.minHeight = (screenHeight * 0.9).toInt()
    }

    private fun playWrongPinAnimation() {
        binding.ivPin0.startAnimation(shakeAnimation)
        binding.ivPin1.startAnimation(shakeAnimation)
        binding.ivPin2.startAnimation(shakeAnimation)
        binding.ivPin3.startAnimation(shakeAnimation)
        binding.ivPin4.startAnimation(shakeAnimation)
        binding.ivPin5.startAnimation(shakeAnimation)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.tag("WHY").d("Dialog: onCreate")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Timber.tag("WHY").d("Dialog: onCreateView")
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        Timber.tag("WHY").d("Dialog: onResume")
    }

    override fun onStart() {
        super.onStart()
        Timber.tag("WHY").d("Dialog: onStart")
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.tag("WHY").d("Dialog: onDestroy")
    }

    override fun onPause() {
        super.onPause()
        Timber.tag("WHY").d("Dialog: onPause")
    }

    override fun onDetach() {
        super.onDetach()
        Timber.tag("WHY").d("Dialog: onDetach")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Timber.tag("WHY").d("Dialog: onAttach")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Timber.tag("WHY").d("Dialog: onDestroyView")
    }
}
