package com.udacity.asteroidradar

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.udacity.asteroidradar.Constants.DEFAULT_END_DATE_DAYS
import com.udacity.asteroidradar.api.ApiClient
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.dao.AsteroidDatabase

class RetrieveAsteroidData(context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params) {
    private val apiClient = ApiClient.create()
    private val asteroidDao = AsteroidDatabase.getInstance(context).asteroidDao()

    override suspend fun doWork(): Result {
        return try {
            retrieveAsteroidData()
            Result.success()
        } catch (throwable: Throwable) {
            Result.failure()
        }
    }

    private suspend fun retrieveAsteroidData() {
        val startDate = Utils.getToday() //Start date is the day after today -> Fixed by commented of the mentor
        val endDate = Utils.getNextNumberOfDate(DEFAULT_END_DATE_DAYS)
        val nextWeekDayDb = asteroidDao.getSpecificDayAsteroid(endDate)
        if (nextWeekDayDb.isNotEmpty()) {
            val asteroidResult =
                apiClient.getAsteroids(startDate, endDate, BuildConfig.API_KEY)
            asteroidResult.body()?.let {
                Log.d("RetrieveAsteroidData", asteroidResult.body().toString())
                val asteroidList = parseAsteroidsJsonResult(it)
                if (asteroidList.isNotEmpty()) {
                    asteroidDao.insertAll(asteroidList)
                }
            }
        }
    }
}
