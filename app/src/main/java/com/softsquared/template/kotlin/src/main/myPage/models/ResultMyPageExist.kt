package com.softsquared.template.kotlin.src.main.myPage.models

import com.google.gson.annotations.SerializedName

data class ResultMyPageExist(
    @SerializedName("type") val type: Int,
    @SerializedName("existOrNot") val existOrNot: Int
)
