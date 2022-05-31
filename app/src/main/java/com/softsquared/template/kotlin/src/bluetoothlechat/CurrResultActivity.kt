package com.softsquared.template.kotlin.src.bluetoothlechat

import android.content.Intent
import android.os.Bundle
import com.softsquared.template.kotlin.config.BaseActivity
import com.softsquared.template.kotlin.databinding.ActivityCurrResultBinding
import com.softsquared.template.kotlin.src.main.MainActivity

class CurrResultActivity : BaseActivity<ActivityCurrResultBinding>(ActivityCurrResultBinding::inflate){
    //val binding by lazy { ActivityCurrResultBinding.inflate(layoutInflater) }
    var startDate: String? = null
    var startTime: String? = null
    var brushing_time: Long ?= 0 // 양치 소요 시간
    var score: Double ?= 0.0 // 양치 점수
    var min = 0
    var sec = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_curr_result)
        setTitle("양치 결과")
        startDate = intent.getStringExtra("startDate") // 년 월 일
        startTime = intent.getStringExtra("startTime") // 시 분 초
        score = intent.getDoubleExtra("score",0.0)
        brushing_time = intent.getLongExtra("brushing_time", 0)
        sec = brushing_time!!.toInt()
        min = sec / 60
        sec %= 60
        binding.brushingDate.text = startDate
        binding.brushingExactTime.text = startTime
        binding.brushingSpendTime.text = " $min" +"분 $sec" + "초"
        binding.scoreTxt.text = score.toString() + '점'
        binding.feedbackTxt.text = "오른쪽 상악부 양치질이 더 필요합니다."
        binding.btnOk.setOnClickListener{
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        //val okBtn = findViewById(R.id.btn_ok2) as Button
//        okBtn.setOnClickListener{
//            startActivity(Intent(this, MainActivity::class.java))
//            finish()
//        }
    }

}