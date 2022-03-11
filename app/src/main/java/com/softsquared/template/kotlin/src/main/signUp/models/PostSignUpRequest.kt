package com.softsquared.template.kotlin.src.main.signUp.models

import com.google.gson.annotations.SerializedName

data class PostSignUpRequest(
        @SerializedName("nickname") val nickname: String,
        @SerializedName("email") val email: String,
        @SerializedName("pwd") val pwd: String
)