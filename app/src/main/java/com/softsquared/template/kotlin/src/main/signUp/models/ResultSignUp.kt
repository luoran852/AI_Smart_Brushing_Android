package com.softsquared.template.kotlin.src.main.signUp.models

import com.google.gson.annotations.SerializedName

data class ResultSignUp(
        @SerializedName("jwt") val jwt: String,
        @SerializedName("userIdx") val userIdx: Int

)