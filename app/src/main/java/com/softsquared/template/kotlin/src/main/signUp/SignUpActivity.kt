package com.softsquared.template.kotlin.src.main.signUp

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.softsquared.template.kotlin.config.BaseActivity
import com.softsquared.template.kotlin.databinding.ActivitySignupBinding
import com.softsquared.template.kotlin.src.main.LoginActivity
import com.softsquared.template.kotlin.src.main.MainActivity
import com.softsquared.template.kotlin.src.splash.SplashActivity

class SignUpActivity : BaseActivity<ActivitySignupBinding>(ActivitySignupBinding::inflate) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 뒤로가기 버튼
        binding.signUpBackBtn.setOnClickListener {
            startActivity(Intent(this, SplashActivity::class.java))
            finish()
        }

        // 회원가입 버튼
        binding.signupSignUpBtn.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        binding.btmLoginBtn.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

    }
}