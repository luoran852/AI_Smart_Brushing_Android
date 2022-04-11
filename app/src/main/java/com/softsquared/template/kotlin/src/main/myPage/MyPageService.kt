package com.softsquared.template.kotlin.src.main.myPage

import android.content.ContentValues.TAG
import android.util.Log
import com.softsquared.template.kotlin.config.ApplicationClass
import com.softsquared.template.kotlin.src.main.myPage.models.MyPageExistResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyPageService(val view: MyPageFragmentView) {

    fun tryGetMyPageExist(type : Int, userIdx : Int, year : Int, month : Int, day : Int) {
        val myPageRetrofitInterface = ApplicationClass.sRetrofit.create(MyPageRetrofitInterface::class.java)
        myPageRetrofitInterface.getMyPageExist(type, userIdx, year, month, day).enqueue(object :
            Callback<MyPageExistResponse> {
            override fun onResponse(
                call: Call<MyPageExistResponse>,
                response: Response<MyPageExistResponse>
            ) {
                Log.e(TAG, "onResponse: tryGetMyPageExist 성공, ${response.message()}")
                view.onGetMyPageExistSuccess(response.body() as MyPageExistResponse, type, userIdx, year, month, day)
            }

            override fun onFailure(call: Call<MyPageExistResponse>, t: Throwable) {
                Log.e(TAG, "onFailure: tryGetMyPageExist 실패, ${t.message}")
                view.onGetMyPageExistFailure(t.message ?: "통신 오류", this as MyPageExistResponse)
            }

        })
    }

}