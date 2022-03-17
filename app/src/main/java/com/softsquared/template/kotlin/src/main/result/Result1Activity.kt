package com.softsquared.template.kotlin.src.main.result

import android.content.Intent
import android.os.Bundle
import com.softsquared.template.kotlin.config.BaseActivity
import com.softsquared.template.kotlin.databinding.ActivityResult1Binding

class Result1Activity : BaseActivity<ActivityResult1Binding>(ActivityResult1Binding::inflate) {

    val TAG : String = "태그"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 뒤로가기 버튼
        binding.backBtn.setOnClickListener {
            finish()
        }

    }
}