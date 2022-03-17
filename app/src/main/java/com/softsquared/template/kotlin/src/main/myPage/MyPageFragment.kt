package com.softsquared.template.kotlin.src.main.myPage

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.CalendarView
import android.widget.ListView
import android.widget.TextView
import com.softsquared.template.kotlin.R
import com.softsquared.template.kotlin.config.BaseFragment
import com.softsquared.template.kotlin.databinding.FragmentMyPageBinding
import com.softsquared.template.kotlin.src.main.result.Result1Activity
import com.softsquared.template.kotlin.src.splash.SplashActivity


class MyPageFragment :
    BaseFragment<FragmentMyPageBinding>(FragmentMyPageBinding::bind, R.layout.fragment_my_page) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dateText: TextView
        var calendarView: CalendarView
        var calendar: Calendar

        calendar = Calendar.getInstance()
        dateText = view.findViewById(R.id.date) as TextView
        calendarView = view.findViewById(R.id.calendarView2) as CalendarView

        val date = calendar[Calendar.YEAR].toString() + "년 " + (calendar[Calendar.MONTH] + 1) + "월 " + calendar[Calendar.DAY_OF_MONTH] + "일"
        dateText.setText(date)

        calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            val date = year.toString() + "년 " + (month + 1) + "월 " + dayOfMonth + "일"
            dateText.setText(date)
        }

        binding.cardviewLayout1.setOnClickListener {
            Log.e(TAG, "onViewCreated: ", )
            activity?.let {
                val intent = Intent(activity, Result1Activity::class.java)
                startActivity(intent)
            }

        }

    }
}