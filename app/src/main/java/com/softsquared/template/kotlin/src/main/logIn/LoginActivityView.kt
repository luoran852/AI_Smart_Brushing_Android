package com.softsquared.template.kotlin.src.main.logIn

import com.softsquared.template.kotlin.src.main.logIn.models.LoginResponse

interface LoginActivityView {

    fun onPostLoginSuccess(response: LoginResponse)

    fun onPostLoginFailure(message: String, response: LoginResponse)
}