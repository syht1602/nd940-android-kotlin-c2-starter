package com.udacity.asteroidradar

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class Utils {
    companion object {
        private fun convertDate(date: Date): String {
            val dateFormat = SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT, Locale.getDefault())
            return dateFormat.format(date)
        }
        fun getToday(): String {
            val calendar = Calendar.getInstance()
            return convertDate(calendar.time)
        }
        fun getNextNumberOfDate(days: Int): String {
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_YEAR, days)
            return convertDate(calendar.time)
        }
    }
}