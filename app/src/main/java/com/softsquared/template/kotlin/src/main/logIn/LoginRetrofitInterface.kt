package com.softsquared.template.kotlin.src.main.logIn

import com.softsquared.template.kotlin.src.main.logIn.models.LoginResponse
import com.softsquared.template.kotlin.src.main.logIn.models.PostLoginRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginRetrofitInterface {

    @POST("/users/logIn")
    fun postLogin(@Body params: PostLoginRequest): Call<LoginResponse>
}