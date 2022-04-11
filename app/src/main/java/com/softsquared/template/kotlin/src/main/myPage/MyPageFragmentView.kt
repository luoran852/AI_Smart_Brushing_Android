package com.softsquared.template.kotlin.src.main.myPage

import com.softsquared.template.kotlin.src.main.myPage.models.MyPageExistResponse

interface MyPageFragmentView {

    fun onGetMyPageExistSuccess(response: MyPageExistResponse, type: Int, userIdx: Int, year: Int, month: Int, day: Int)

    fun onGetMyPageExistFailure(message: String, response: MyPageExistResponse)
}