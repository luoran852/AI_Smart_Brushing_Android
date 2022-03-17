package com.softsquared.template.kotlin.src.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.softsquared.template.kotlin.config.ApplicationClass
import com.softsquared.template.kotlin.config.BaseActivity
import com.softsquared.template.kotlin.databinding.ActivityLoginBinding
import com.softsquared.template.kotlin.src.main.MainActivity
import com.softsquared.template.kotlin.src.main.logIn.LoginActivityView
import com.softsquared.template.kotlin.src.main.logIn.LoginService
import com.softsquared.template.kotlin.src.main.logIn.models.LoginResponse
import com.softsquared.template.kotlin.src.main.logIn.models.PostLoginRequest
import com.softsquared.template.kotlin.src.splash.SplashActivity

class LoginActivity : BaseActivity<ActivityLoginBinding>(ActivityLoginBinding::inflate), LoginActivityView {

    val TAG : String = "태그"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 뒤로가기 버튼
        binding.signUpBackBtn.setOnClickListener {
            finish()
        }

        // 로그인 버튼
        binding.loginLogInBtn.setOnClickListener {
            val email = binding.loginEmailTxt.text.toString()
            val password = binding.loginPwdTxt.text.toString()

            val postRequest = PostLoginRequest(email = email, pwd = password)
            showLoadingDialog(this)
            LoginService(this).tryPostLogin(postRequest)
        }

        // 비밀번호 찾기 버튼
        binding.findPwdBtn.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

    }

    override fun onPostLoginSuccess(response: LoginResponse) {
        dismissLoadingDialog()

        //jwt값 sharedpreference에 저장
        Log.e(TAG, "로그인 성공, jwt = ${response.result.jwt}")

        // SharedPreferences 의 데이터를 저장/편집을 위해 Editor 변수를 선언
        val editor1 = ApplicationClass.sSharedPreferences.edit()

        // key값에 value값을 저장
        editor1.putString(ApplicationClass.X_ACCESS_TOKEN, response.result.jwt)
//        editor1.putString(ApplicationClass.X_ACCESS_TOKEN, "0")

        // 메모리에 있는 데이터를 저장장치에 저장함. commit
        editor1.commit()

        if (response.code == 1000) {
            Log.e(TAG, "onPostLoginSuccess: 로그인 성공")
            response.message?.let { showCustomToast("로그인 성공") }

            //userIdx값 sharedpreference에 저장
            Log.e(TAG, "로그인 성공, userIdx = ${response.result.userIdx}")

            // SharedPreferences 의 데이터를 저장/편집을 위해 Editor 변수를 선언
            val editor2 = ApplicationClass.sSharedPreferences.edit()

            // key값에 value값을 저장
            editor2.putInt("userIdx", response.result.userIdx)

            // 메모리에 있는 데이터를 저장장치에 저장함. commit
            editor2.commit()

            Log.e(TAG, "userIdx 저장됐는지 확인: ${ApplicationClass.sSharedPreferences.getInt("userIdx", 0)}")

            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    override fun onPostLoginFailure(message: String, response: LoginResponse) {
        dismissLoadingDialog()
        Log.e(TAG, "onPostLoginFailure: 로그인 실패")
        when(response.code) {

            // 이메일을 입력해주세요.
            2015 -> {
                showCustomToast("$message")
            }

            // 비밀번호를 입력해주세요.
            2019 -> {
                showCustomToast("$message")
            }

            // 없는 아이디거나 비밀번호가 틀렸습니다.
            3014 -> {
                showCustomToast("$message")
            }

            // 비밀번호 복호화에 실패하였습니다.
            4012 -> {
                showCustomToast("$message")
            }

        }
    }
}