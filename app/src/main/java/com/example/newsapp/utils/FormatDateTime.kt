package com.example.newsapp.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class FormatDateTime {
    companion object{
        fun format(date: String): String{
            val currentDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            currentDate.timeZone = TimeZone.getTimeZone("UTC")

            val date: Date = currentDate.parse(date)!!

            val newDate = SimpleDateFormat("dd/MM/yyyy, HH:mm", Locale.getDefault())
            newDate.timeZone = TimeZone.getTimeZone("Asia/Ha_Noi")

            return newDate.format(date)
        }
    }
}