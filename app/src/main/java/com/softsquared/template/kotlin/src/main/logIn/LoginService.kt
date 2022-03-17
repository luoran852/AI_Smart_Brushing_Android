package com.softsquared.template.kotlin.src.main.logIn

import android.content.ContentValues.TAG
import android.util.Log
import com.softsquared.template.kotlin.config.ApplicationClass
import com.softsquared.template.kotlin.src.main.logIn.models.LoginResponse
import com.softsquared.template.kotlin.src.main.logIn.models.PostLoginRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginService(val view: LoginActivityView) {

    val TAG : String = "태그"
    fun tryPostLogin(postLoginRequest: PostLoginRequest) {
        val loginRetrofitInterface = ApplicationClass.sRetrofit.create(LoginRetrofitInterface::class.java)
        loginRetrofitInterface.postLogin(postLoginRequest).enqueue(object :
            Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                view.onPostLoginSuccess(response.body() as LoginResponse)
                Log.e(TAG, "jwt? ${response.body()}")
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Log.e(TAG, "LoginService-onFailure")
                view.onPostLoginFailure(t.message ?: "통신 오류", this as LoginResponse)
            }


        })
    }

}