package com.softsquared.template.kotlin.src.main.signUp

import com.softsquared.template.kotlin.src.main.signUp.models.SignUpResponse

interface SignUpActivityView {

    fun onPostSignUpSuccess(response: SignUpResponse)

    fun onPostSignUpFailure(message: String, response: SignUpResponse)

}