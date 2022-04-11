package com.softsquared.template.kotlin.src.main.dateDetail

import android.util.Log
import com.softsquared.template.kotlin.config.ApplicationClass
import com.softsquared.template.kotlin.src.main.dateDetail.models.DateDetailResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DateDetailService(val view: DateDetailActivityView) {

    val TAG : String = "태그"
    fun tryGetDateDetail(type : Int, userIdx : Int, year : Int, month : Int, day : Int) {
        val dateDetailRetrofitInterface = ApplicationClass.sRetrofit.create(DateDetailRetrofitInterface::class.java)
        dateDetailRetrofitInterface.getDateDetail(type, userIdx, year, month, day).enqueue(object :
            Callback<DateDetailResponse> {
            override fun onResponse(call: Call<DateDetailResponse>, response: Response<DateDetailResponse>) {
                view.onGetDateDetailSuccess(response.body() as DateDetailResponse)
                Log.e(TAG, "onResponse: tryGetDateDetail 성공, ${response.message()}")
            }

            override fun onFailure(call: Call<DateDetailResponse>, t: Throwable) {
                Log.e(TAG, "tryGetDateDetail-onFailure")
                view.onGetDateDetailFailure(t.message ?: "통신 오류", this as DateDetailResponse)
            }


        })
    }

}