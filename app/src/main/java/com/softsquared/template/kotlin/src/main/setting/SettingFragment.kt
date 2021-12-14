package com.softsquared.template.kotlin.src.main.myPage

import android.os.Bundle
import android.view.View
import com.softsquared.template.kotlin.R
import com.softsquared.template.kotlin.config.BaseFragment
import com.softsquared.template.kotlin.databinding.FragmentSettingBinding

class SettingFragment : BaseFragment<FragmentSettingBinding>(FragmentSettingBinding::bind, R.layout.fragment_setting) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 개인정보 변경
        binding.firstLayer.setOnClickListener {
        }

        // 알림 설정
        binding.secondLayer.setOnClickListener {
        }

        // 로그아웃
        binding.thirdLayer.setOnClickListener {
        }

        // 버전 정보
        binding.fourthLayer.setOnClickListener {
        }

        // 오류 신고
        binding.fifthLayer.setOnClickListener {
        }

        // 제작자
        binding.sixthLayer.setOnClickListener {
        }

        // 회원탈퇴
        binding.seventhLayer.setOnClickListener {
        }
    }
}