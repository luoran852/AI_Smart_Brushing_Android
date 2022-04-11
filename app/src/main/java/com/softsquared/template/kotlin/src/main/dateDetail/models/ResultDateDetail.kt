package com.softsquared.template.kotlin.src.main.dateDetail.models

import com.google.gson.annotations.SerializedName

data class ResultDateDetail(
    @SerializedName("idx") val idx: Int,
    @SerializedName("brushDate") val brushDate: String,
    @SerializedName("exactTime") val exactTime: String,
    @SerializedName("score") val score: Int,
    @SerializedName("brushtime") val brushtime: Int,
    @SerializedName("feedbackMsg") val feedbackMsg: String
)
