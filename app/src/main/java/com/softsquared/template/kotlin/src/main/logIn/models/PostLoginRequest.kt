package com.softsquared.template.kotlin.src.main.logIn.models

import com.google.gson.annotations.SerializedName

data class PostLoginRequest(
    @SerializedName("email") val email: String,
    @SerializedName("pwd") val pwd: String
)
