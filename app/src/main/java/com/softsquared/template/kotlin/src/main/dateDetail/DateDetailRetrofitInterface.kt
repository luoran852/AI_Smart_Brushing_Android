package com.softsquared.template.kotlin.src.main.dateDetail

import com.softsquared.template.kotlin.src.main.dateDetail.models.DateDetailResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface DateDetailRetrofitInterface {

    @GET("/users/feedback/time/detail")
    fun getDateDetail(
        @Query("type") type: Int?,
        @Query("userIdx") userIdx: Int?,
        @Query("year") year: Int?,
        @Query("month") month: Int?,
        @Query("day") day: Int?
    ): Call<DateDetailResponse>

}