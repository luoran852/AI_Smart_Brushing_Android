package com.softsquared.template.kotlin.src.main.signUp

import com.softsquared.template.kotlin.src.main.signUp.models.PostSignUpRequest
import com.softsquared.template.kotlin.src.main.signUp.models.SignUpResponse

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface SignUpRetrofitInterface {

    @POST("/users/sign-up")
    fun postSignUp(@Body params:PostSignUpRequest): Call<SignUpResponse>
}