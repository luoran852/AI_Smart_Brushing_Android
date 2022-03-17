package com.softsquared.template.kotlin.src.main.signUp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.softsquared.template.kotlin.config.ApplicationClass
import com.softsquared.template.kotlin.config.BaseActivity
import com.softsquared.template.kotlin.databinding.ActivitySignupBinding
import com.softsquared.template.kotlin.src.main.LoginActivity
import com.softsquared.template.kotlin.src.main.MainActivity
import com.softsquared.template.kotlin.src.main.signUp.models.PostSignUpRequest
import com.softsquared.template.kotlin.src.main.signUp.models.SignUpResponse
import com.softsquared.template.kotlin.src.splash.SplashActivity

class SignUpActivity : BaseActivity<ActivitySignupBinding>(ActivitySignupBinding::inflate), SignUpActivityView {

    val TAG : String = "태그"

    //login activity에 저장한 X_ACCESS_TOKEN
    val XACCESSTOKEN = ApplicationClass.sSharedPreferences.getString(ApplicationClass.X_ACCESS_TOKEN, "")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.e(TAG, "XACCESSTOKEN = $XACCESSTOKEN")

        if (!(XACCESSTOKEN.equals(""))) {
            val userIdx = ApplicationClass.sSharedPreferences.getInt("userIdx", 0) //sf에 저장된 userIdx 가져오기
        }

        // 뒤로가기 버튼
        binding.signUpBackBtn.setOnClickListener {
            finish()
        }

        // 회원가입 버튼
        binding.signupSignUpBtn.setOnClickListener {
            val email = binding.emailTxt.text.toString()
            val password = binding.pwdTxt.text.toString()
            val nickname = binding.nicknameTxt.text.toString()
            val postRequest = PostSignUpRequest(nickname = nickname, email = email, pwd = password)
            showLoadingDialog(this)
            SignUpService(this).tryPostSignUp(postRequest)
        }

        // 로그인 버튼
        binding.btmLoginBtn.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

    }

    override fun onPostSignUpSuccess(response: SignUpResponse) {
        dismissLoadingDialog()

        if (response.code == 1000) {
            Log.e(TAG, "onPostSignUpSuccess: 회원가입 성공")
            response.message?.let { showCustomToast("회원가입 성공") }

            Log.e(TAG, "회원가입 성공, userIdx = ${response.result.userIdx}")
            Log.e(TAG, "jwt = ${response.result.jwt}")

            // SharedPreferences 의 데이터를 저장/편집을 위해 Editor 변수를 선언
            val editor = ApplicationClass.sSharedPreferences.edit()

            // key값에 value값을 저장
            editor.putInt("userIdx", response.result.userIdx)

            // 메모리에 있는 데이터를 저장장치에 저장함. commit
            editor.commit()

            Log.e(TAG, "userIdx 저장됐는지 확인: ${ApplicationClass.sSharedPreferences.getInt("userIdx", 0)}")

            finish()
        }
    }

    override fun onPostSignUpFailure(message: String, response: SignUpResponse) {
        dismissLoadingDialog()
        Log.e(TAG, "onPostSignUpFailure: 회원가입 실패")

        when(response.code) {

            // 이메일을 입력해주세요.
            2015 -> {
                showCustomToast("$message")
            }

            // 이메일 형식을 확인해주세요.
            2016 -> {
                showCustomToast("$message")
            }

            // 중복된 이메일입니다.
            2017 -> {
                showCustomToast("$message")
            }

            // 비밀번호 형식을 확인해주세요.(최소 8자, 최소 하나의 문자, 하나의 숫자 및 하나의 특수 문자)
            2018 -> {
                showCustomToast("$message")
            }

            // 데이터베이스 연결에 실패하였습니다.
            4000 -> {
                showCustomToast("$message")
            }

            // 비밀번호 암호화에 실패하였습니다.
            4011 -> {
                showCustomToast("$message")
            }

        }
        response.message?.let { showCustomToast("$message") }
    }

}