package com.softsquared.template.kotlin.src.main.dateDetail

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.softsquared.template.kotlin.config.ApplicationClass
import com.softsquared.template.kotlin.config.BaseActivity
import com.softsquared.template.kotlin.databinding.ActivityDateDetailBinding
import com.softsquared.template.kotlin.src.main.dateDetail.models.DateDetailResponse

class DateDetailActivity : BaseActivity<ActivityDateDetailBinding>(ActivityDateDetailBinding::inflate), DateDetailActivityView {

    val TAG : String = "태그"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val userIdx = ApplicationClass.sSharedPreferences.getInt("userIdx", 0) //sf에 저장된 userIdx 가져오기
//    val type = ApplicationClass.sSharedPreferences.getInt("type", 0)
//    val calanderYear = ApplicationClass.sSharedPreferences.getInt("year", 0)
//    val calendarMonth = ApplicationClass.sSharedPreferences.getInt("month", 0)
//    val calendarDay = ApplicationClass.sSharedPreferences.getInt("day", 0)

//    val secondIntent = intent
        val type = intent.getIntExtra("type", 0)
        val year = intent.getIntExtra("year", 0)
        val month = intent.getIntExtra("month", 0)
        val day = intent.getIntExtra("day", 0)


        Log.e(TAG, "DateDetailActivity: userIdx = ${userIdx}, type = ${type}," +
                "year = ${year}, month = ${month}, day = ${day}")

        // 뒤로가기 버튼
        binding.backBtn.setOnClickListener {
            finish()
        }

        showLoadingDialog(this)
        DateDetailService(this).tryGetDateDetail(type, userIdx, year, month, day)

    }

    override fun onGetDateDetailSuccess(response: DateDetailResponse) {
        dismissLoadingDialog()

        Log.e(TAG, "onGetDateDetailSuccess: 날짜별 데이터 조회 통신 성공")
        if (response.code == 1000) {
            Log.e(TAG, "onGetDateDetailSuccess: brushDate = ${response.result.brushDate}, " +
                    "exactTime = ${response.result.exactTime}, " +
                    "brushtime = ${response.result.brushtime.toString() + '초'}, " +
                    "score = ${response.result.score.toString() + '점'}, " +
                    "feedbackMsg = ${response.result.feedbackMsg}")
            binding.brushingDate.text = response.result.brushDate
            binding.brushingExactTime.text = response.result.exactTime
            binding.brushingSpendTime.text = response.result.brushtime.toString() + '초'
            binding.scoreTxt.text = response.result.score.toString() + '점'
            binding.feedbackTxt.text = response.result.feedbackMsg
        }
    }

    override fun onGetDateDetailFailure(message: String, dateDetailResponse: DateDetailResponse) {
        dismissLoadingDialog()
        Log.e(TAG, "onGetDateDetailFailure: 날짜별 데이터 조회 통신 실패")
    }
}