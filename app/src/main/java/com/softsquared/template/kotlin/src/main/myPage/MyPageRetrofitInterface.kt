package com.softsquared.template.kotlin.src.main.myPage

import com.softsquared.template.kotlin.src.main.myPage.models.MyPageExistResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface MyPageRetrofitInterface {

    @GET("/users/feedback/time/check")
    fun getMyPageExist(
        @Query("type") type: Int?,
        @Query("userIdx") userIdx: Int?,
        @Query("year") year: Int?,
        @Query("month") month: Int?,
        @Query("day") day: Int?
    ): Call<MyPageExistResponse>

}