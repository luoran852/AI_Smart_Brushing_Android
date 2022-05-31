package com.softsquared.template.kotlin.src.main.dateDetail

import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.softsquared.template.kotlin.R
import com.softsquared.template.kotlin.config.ApplicationClass
import com.softsquared.template.kotlin.config.BaseActivity
import com.softsquared.template.kotlin.databinding.ActivityDateDetailBinding
import com.softsquared.template.kotlin.src.main.dateDetail.models.DateDetailResponse

class DateDetailActivity : BaseActivity<ActivityDateDetailBinding>(ActivityDateDetailBinding::inflate), DateDetailActivityView {

    val TAG : String = "태그"
    var startDate: String? = null
    var startTime: String? = null
    var score: Double ?= 0.0 // 양치 점수
    var brushing_time: Long ?= 0 // 양치 소요 시간
    var min = 0
    var sec = 0

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


        startDate = intent.getStringExtra("startDate") // 년 월 일
        if(startDate == null){
            startDate = "2022년 05월 31일"
        }
        startTime = intent.getStringExtra("startTime") // 시 분 초
        if(startTime == null){
            startTime = "13시 35분 42초"
        }
        score = intent.getDoubleExtra("score",60.25) // 점수
        brushing_time = intent.getLongExtra("brushing_time", 200) // 양치지속시간
        sec = brushing_time!!.toInt()
        min = sec / 60
        sec %= 60

        binding.brushingDate.text = startDate
        binding.brushingExactTime.text = startTime
        binding.brushingSpendTime.text = " $min" +"분 $sec" + "초"
        binding.scoreTxt.text = score.toString() + '점'
        binding.feedbackTxt.text = "오른쪽 상악부 양치질이 더 필요합니다."

        Log.e(TAG, "DateDetailActivity: userIdx = ${userIdx}, type = ${type}," +
                "year = ${year}, month = ${month}, day = ${day}")

        // 뒤로가기 버튼
        binding.backBtn.setOnClickListener {
            finish()
        }

        showLoadingDialog(this)
        DateDetailService(this).tryGetDateDetail(type, userIdx, year, month, day)
        val okBtn = findViewById(R.id.btn_ok) as Button
        okBtn.setOnClickListener{
            finish()
        }
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



            // 나중에 주석풀기
//            binding.brushingDate.text = response.result.brushDate
//            binding.brushingExactTime.text = response.result.exactTime
//            binding.brushingSpendTime.text = response.result.brushtime.toString() + '초'
//            binding.scoreTxt.text = response.result.score.toString() + '점'
//            binding.feedbackTxt.text = response.result.feedbackMsg

        }
    }

    override fun onGetDateDetailFailure(message: String, dateDetailResponse: DateDetailResponse) {
        dismissLoadingDialog()
        Log.e(TAG, "onGetDateDetailFailure: 날짜별 데이터 조회 통신 실패")
    }
}