package com.softsquared.template.kotlin.src.main.myPage

import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.CalendarView
import android.widget.TextView
import com.softsquared.template.kotlin.R
import com.softsquared.template.kotlin.config.ApplicationClass
import com.softsquared.template.kotlin.config.BaseFragment
import com.softsquared.template.kotlin.databinding.FragmentMyPageBinding
import com.softsquared.template.kotlin.src.main.myPage.models.MyPageExistResponse
import com.softsquared.template.kotlin.src.main.dateDetail.DateDetailActivity


class MyPageFragment :
    BaseFragment<FragmentMyPageBinding>(FragmentMyPageBinding::bind, R.layout.fragment_my_page), MyPageFragmentView {

    //login activity에 저장한 X_ACCESS_TOKEN
//    val XACCESSTOKEN = ApplicationClass.sSharedPreferences.getString(ApplicationClass.X_ACCESS_TOKEN, "")

    val TAG : String = "태그"
    val userIdx = ApplicationClass.sSharedPreferences.getInt("userIdx", 0) //sf에 저장된 userIdx 가져오기

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var dateText: TextView
        var calendarView: CalendarView
        var calendar: Calendar
        var calanderYear: Int = 0
        var calendarMonth: Int = 0
        var calendarDay: Int = 0

        calendar = Calendar.getInstance()
        dateText = view.findViewById(R.id.date) as TextView
        calendarView = view.findViewById(R.id.calendarView2) as CalendarView

        val date = calendar[Calendar.YEAR].toString() + "년 " + (calendar[Calendar.MONTH] + 1) + "월 " + calendar[Calendar.DAY_OF_MONTH] + "일"
        dateText.setText(date)

        calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            val date = year.toString() + "년 " + (month + 1) + "월 " + dayOfMonth + "일"
            dateText.setText(date)
            calanderYear = year
            calendarMonth = month + 1
            calendarDay = dayOfMonth

//            // SharedPreferences 의 데이터를 저장/편집을 위해 Editor 변수를 선언
//            val editor1 = ApplicationClass.sSharedPreferences.edit()
//
//            // key값에 value값을 저장
//            editor1.putInt("type", type)
//            editor1.putInt("year", calanderYear)
//            editor1.putInt("month", calendarMonth)
//            editor1.putInt("day", calendarDay)
//
//            // 메모리에 있는 데이터를 저장장치에 저장함. commit
//            editor1.commit()

        }

        // 아침 layout
        binding.cardviewLayout1.setOnClickListener {
            showLoadingDialog(context!!)
            MyPageService(this).tryGetMyPageExist(1, userIdx, calanderYear,
                calendarMonth, calendarDay)
        }

        // 점심 layout
        binding.cardviewLayout2.setOnClickListener {
            showLoadingDialog(context!!)
            MyPageService(this).tryGetMyPageExist(2, userIdx, calanderYear,
                calendarMonth, calendarDay)
        }

        // 저녁 layout
        binding.cardviewLayout3.setOnClickListener {
            showLoadingDialog(context!!)
            MyPageService(this).tryGetMyPageExist(3, userIdx, calanderYear,
                calendarMonth, calendarDay)
        }

//
//            // type == 1 (아침: 새벽 4시 - 오후 12시 이전) --> 3 < hour < 12
//            if (hour in 4..11)
//                type = 1
//
//            // type == 2 (점심: 오후 12시 - 오후 4시 이전) --> 11 < hour < 16
//            else if (hour in 12..15)
//                type = 2
//
//            // type == 3 (저녁: 새벽 12시 - 4시 이전 + 오후 4시 - 밤12시 이전) --> hour < 4 or (15 < hour < 24)
//            else if ((hour < 4) || (hour in 16..23))
//                type = 3

//        showLoadingDialog(context!!)
//        MyPageService(this).tryGetMyPageExist(type, userIdx, calendar[Calendar.YEAR],
//            calendar[Calendar.MONTH], calendar[Calendar.DAY_OF_MONTH])
//
//        // SharedPreferences 의 데이터를 저장/편집을 위해 Editor 변수를 선언
//        val editor1 = ApplicationClass.sSharedPreferences.edit()
//
//        // key값에 value값을 저장
//        editor1.putInt("type", type)
//        editor1.putInt("year", calendar[Calendar.YEAR])
//        editor1.putInt("month", calendar[Calendar.MONTH])
//        editor1.putInt("day", calendar[Calendar.DAY_OF_MONTH])
//
//        // 메모리에 있는 데이터를 저장장치에 저장함. commit
//        editor1.commit()
    }

    override fun onGetMyPageExistSuccess(response: MyPageExistResponse, type: Int, userIdx: Int,
                                         year: Int, month: Int, day: Int)
    {
        dismissLoadingDialog()
        if (response.code == 1000) {
            Log.e(TAG, "onGetMyPageExistSuccess: 날짜별 데이터 존재여부 통신 성공")

            if (response.result.existOrNot == 0) {
                Log.e(TAG, "existOrNot == 0")
                Log.e(TAG, "MyPageFragment: userIdx = ${userIdx}, type = ${type}," +
                        "year = ${year}, month = ${month}, day = ${day}")
                showCustomToast("양치 정보가 존재하지 않습니다.")
            }
            else if (response.result.existOrNot == 1) {
                Log.e(TAG, "existOrNot == 1")
                val type = response.result.type
                Log.e(TAG, "MyPageFragment: userIdx = ${userIdx}, type = ${type}," +
                        "year = ${year}, month = ${month}, day = ${day}")
                val intent1 = Intent(activity, DateDetailActivity::class.java)
                intent1.putExtra("type", type)
                intent1.putExtra("year", year)
                intent1.putExtra("month", month)
                intent1.putExtra("day", day)
                startActivity(intent1)
            }

        }
    }

    override fun onGetMyPageExistFailure(message: String, response: MyPageExistResponse) {
        dismissLoadingDialog()
        Log.e(TAG, "onGetMyPageExistFailure: 날짜별 데이터 존재여부 조회 실패")
        when(response.code) {

            // 데이터베이스 연결에 실패하였습니다.
            4000 -> {
                showCustomToast("$message")
            }

        }
    }



}