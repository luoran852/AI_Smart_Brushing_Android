package com.softsquared.template.kotlin.src.main.home

import android.os.Bundle
import android.util.Log
import android.view.View
import com.softsquared.template.kotlin.R
import com.softsquared.template.kotlin.config.BaseFragment
import com.softsquared.template.kotlin.databinding.FragmentHomeBinding
import com.softsquared.template.kotlin.src.main.home.models.PostSignUpRequest
import com.softsquared.template.kotlin.src.main.home.models.SignUpResponse
import com.softsquared.template.kotlin.src.main.home.models.UserResponse

class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::bind, R.layout.fragment_home),
        HomeFragmentView {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        binding.homeButtonTryGetJwt.setOnClickListener {
//            showLoadingDialog(context!!)
//            HomeService(this).tryGetUsers()
//        }
//
//        binding.homeBtnTryPostHttpMethod.setOnClickListener {
//            val email = binding.homeEtId.text.toString()
//            val password = binding.homeEtPw.text.toString()
//            val nickname = binding.homeEtNickname.text.toString()
//            val postRequest = PostSignUpRequest(nickname = nickname, email = email, pwd = password)
//            showLoadingDialog(context!!)
//            HomeService(this).tryPostSignUp(postRequest)
//        }
    }

    override fun onGetUserSuccess(response: UserResponse) {
        TODO("Not yet implemented")
    }

    override fun onGetUserFailure(message: String) {
        TODO("Not yet implemented")
    }

    override fun onPostSignUpSuccess(response: SignUpResponse) {
        TODO("Not yet implemented")
    }

    override fun onPostSignUpFailure(message: String) {
        TODO("Not yet implemented")
    }
//
//    override fun onGetUserSuccess(response: UserResponse) {
//        dismissLoadingDialog()
//        for (User in response.result) {
//            Log.d("HomeFragment", User.toString())
//        }
//        binding.homeButtonTryGetJwt.text = response.message
//        showCustomToast("Get JWT 성공")
//    }
//
//    override fun onGetUserFailure(message: String) {
//        dismissLoadingDialog()
//        showCustomToast("오류 : $message")
//    }
//
//    override fun onPostSignUpSuccess(response: SignUpResponse) {
//        dismissLoadingDialog()
//        binding.homeBtnTryPostHttpMethod.text = response.message
//        response.message?.let { showCustomToast(it) }
//    }
//
//    override fun onPostSignUpFailure(message: String) {
//        dismissLoadingDialog()
//        showCustomToast("오류 : $message")
//    }
}