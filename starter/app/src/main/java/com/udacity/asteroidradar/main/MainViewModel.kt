package com.udacity.asteroidradar.main

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.udacity.asteroidradar.BuildConfig
import com.udacity.asteroidradar.Constants.DEFAULT_END_DATE_DAYS
import com.udacity.asteroidradar.RetrieveAsteroidData
import com.udacity.asteroidradar.Utils.Companion.getNextNumberOfDate
import com.udacity.asteroidradar.Utils.Companion.getToday
import com.udacity.asteroidradar.api.ApiClient
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.dao.AsteroidDatabase
import com.udacity.asteroidradar.domain.models.Asteroid
import com.udacity.asteroidradar.domain.models.PictureOfDay
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val TAG = "MainViewModel"
    private val apiClient = ApiClient.create()
    private val asteroidDao = AsteroidDatabase.getInstance(application).asteroidDao()
    private val mWorkManager = WorkManager.getInstance(application)
    private var viewAsteroidStatus = ViewAsteroidStatus.UNDEFINED

    private val _pictureOfDay = MutableLiveData<PictureOfDay>()
    val pictureOfDay: LiveData<PictureOfDay>
        get() = _pictureOfDay

    private val _asteroidData = MutableLiveData<List<Asteroid>>().apply { value = emptyList() }
    val asteroidData: LiveData<List<Asteroid>>
        get() = _asteroidData

    init {
        startWork()
        val nextWeekDay = getNextNumberOfDate(DEFAULT_END_DATE_DAYS)
        val nextWeekDayDb = asteroidDao.getSpecificDayAsteroid(nextWeekDay)
        viewModelScope.launch {
            getPictureOfDay()
            if (nextWeekDayDb.isNotEmpty()) {
                getAsteroidFromDb()
            } else {
                retrieveAsteroidData()
            }
        }
    }

    private fun startWork() {
        val retrieveDataConstraints =
            Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresCharging(true)
                .setRequiresBatteryNotLow(true)
                .build()
        val workRequest =
            PeriodicWorkRequestBuilder<RetrieveAsteroidData>(1, TimeUnit.DAYS).setConstraints(
                retrieveDataConstraints
            ).build()
        mWorkManager.enqueueUniquePeriodicWork(
            "RetrieveAsteroidData", ExistingPeriodicWorkPolicy.KEEP, workRequest
        )
    }

    private suspend fun getPictureOfDay() {
        try {
            val pictureOfDayResult = apiClient.getPictureOfDay(BuildConfig.API_KEY)
            if (pictureOfDayResult.isSuccessful) {
                _pictureOfDay.value = pictureOfDayResult.body()
                Log.d(TAG, pictureOfDayResult.body().toString())
            } else {
                Log.d(TAG, pictureOfDayResult.errorBody().toString())
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private suspend fun retrieveAsteroidData() {
        val today = getToday()
        val nextWeekDay = getNextNumberOfDate(DEFAULT_END_DATE_DAYS)

        try {
            val asteroidResult = apiClient.getAsteroids(today, nextWeekDay, BuildConfig.API_KEY)
            Log.d(TAG, "asteroidList from api: ${asteroidResult.body().toString()}")
            asteroidResult.body()?.let {
                val asteroidList = parseAsteroidsJsonResult(it)
                if (asteroidList.isNotEmpty()) {
                    viewModelScope.launch {
                        asteroidDao.insertAll(asteroidList)
                        getAsteroidFromDb()
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun getAsteroidFromDb() {
        if (viewAsteroidStatus == ViewAsteroidStatus.VIEW_SAVED) {
            return
        }
        clearListAsteroid()
        val asteroidList = asteroidDao.getAllAsteroids()
        Log.d(TAG, "asteroidList from DB: $asteroidList")
        _asteroidData.value = asteroidList
        viewAsteroidStatus = ViewAsteroidStatus.VIEW_SAVED
    }

    fun getTodayAsteroidFromDb() {
        if (viewAsteroidStatus == ViewAsteroidStatus.VIEW_TODAY) {
            return
        }
        clearListAsteroid()
        val todayAsteroid = asteroidDao.getSpecificDayAsteroid(getToday())
        _asteroidData.value = todayAsteroid
        viewAsteroidStatus = ViewAsteroidStatus.VIEW_TODAY
    }

    fun getWeekAsteroidFromDb() {
        if (viewAsteroidStatus == ViewAsteroidStatus.VIEW_WEEK) {
            return
        }
        clearListAsteroid()
        val startDate =
            getNextNumberOfDate(1) // Start date will not include today for week view -> Fixed by commented of the mentor
        val endDate = getNextNumberOfDate(DEFAULT_END_DATE_DAYS)
        val weekAsteroid = asteroidDao.getWeekAsteroid(startDate, endDate)
        _asteroidData.value = weekAsteroid
        viewAsteroidStatus = ViewAsteroidStatus.VIEW_WEEK
    }

    private fun clearListAsteroid() {
        _asteroidData.value = emptyList()
    }

    suspend fun deleteDate(date: String) {
        asteroidDao.delete(date)
    }
}

enum class ViewAsteroidStatus {
    VIEW_WEEK,
    VIEW_TODAY,
    VIEW_SAVED,
    UNDEFINED
}