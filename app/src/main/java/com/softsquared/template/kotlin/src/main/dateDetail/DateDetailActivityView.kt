package com.softsquared.template.kotlin.src.main.dateDetail

import com.softsquared.template.kotlin.src.main.dateDetail.models.DateDetailResponse

interface DateDetailActivityView {

    fun onGetDateDetailSuccess(response: DateDetailResponse)

    fun onGetDateDetailFailure(message: String, dateDetailResponse: DateDetailResponse)

}