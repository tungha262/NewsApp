package com.example.newsapp.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class FormatDateTime {
    companion object {
        fun format(date: String): String {
            val currentDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            currentDate.timeZone = TimeZone.getTimeZone("UTC")

            val parsedDate: Date = currentDate.parse(date)!!

            val newDate = SimpleDateFormat("dd/MM/yyyy, HH:mm", Locale.getDefault())
            newDate.timeZone = TimeZone.getTimeZone("Asia/Ha_Noi")

            return newDate.format(parsedDate)
        }

        fun formatFull(date: String): String {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            inputFormat.timeZone = TimeZone.getTimeZone("UTC")
            val parsedDate = inputFormat.parse(date)!!

            val dayFormat = SimpleDateFormat("EEEE", Locale("vi", "VN"))
            dayFormat.timeZone = TimeZone.getTimeZone("Asia/Ha_Noi")
            val dayOfWeek = dayFormat.format(parsedDate)

            val dateFormat = SimpleDateFormat("dd/MM/yyyy, HH:mm", Locale.getDefault())
            dateFormat.timeZone = TimeZone.getTimeZone("Asia/Ha_Noi")
            val datePart = dateFormat.format(parsedDate)

            return "$dayOfWeek, $datePart"
        }

        fun getCurrentDate(): String {
            val localeVN = Locale("vi", "VN")
            val calendar = Calendar.getInstance()

            val dayOfWeekFormat = SimpleDateFormat("EEEE", localeVN)
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            val month = calendar.get(Calendar.MONTH) + 1
            val year = calendar.get(Calendar.YEAR)
            val dayOfWeek = dayOfWeekFormat.format(calendar.time)

            return "$dayOfWeek, ngày $day tháng $month năm $year"
        }
    }
}
