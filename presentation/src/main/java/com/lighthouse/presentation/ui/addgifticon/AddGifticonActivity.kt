package com.lighthouse.presentation.ui.addgifticon

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.lighthouse.presentation.R
import com.lighthouse.presentation.databinding.ActivityAddGifticonBinding

class AddGifticonActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddGifticonBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_gifticon)
        binding.apply {
            lifecycleOwner = this@AddGifticonActivity
        }
    }
}
