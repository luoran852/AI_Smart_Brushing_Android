package com.softsquared.template.kotlin.src.main.signUp

import com.softsquared.template.kotlin.config.ApplicationClass
import com.softsquared.template.kotlin.src.main.signUp.models.PostSignUpRequest
import com.softsquared.template.kotlin.src.main.signUp.models.SignUpResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignUpService(val view: SignUpActivityView) {

    fun tryPostSignUp(postSignUpRequest:PostSignUpRequest){
        val signUpActivityInterface = ApplicationClass.sRetrofit.create(SignUpActivityInterface::class.java)
        signUpActivityInterface.postSignUp(postSignUpRequest).enqueue(object :
        Callback<SignUpResponse> {
            override fun onResponse(call: Call<SignUpResponse>, response: Response<SignUpResponse>) {
                view.onPostSignUpSuccess(response.body() as SignUpResponse)
            }

            override fun onFailure(call: Call<SignUpResponse>, t: Throwable) {
                view.onPostSignUpFailure(t.message ?: "통신 오류", this as SignUpResponse)
            }
        })
    }

}