package com.softsquared.template.kotlin.src.main.logIn.models

import com.google.gson.annotations.SerializedName

data class ResultLogin(
    @SerializedName("userIdx") val userIdx: Int,
    @SerializedName("jwt") val jwt: String
)
