package com.udacity.asteroidradar.api

import com.udacity.asteroidradar.api.data.AsteroidResponse
import com.udacity.asteroidradar.domain.models.PictureOfDay
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface AsteroidApiService {
    @GET("neo/rest/v1/feed")
    suspend fun getAsteroids(
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String,
        @Query("api_key") apiKey: String
    ): Response<AsteroidResponse>

    @GET("planetary/apod")
    suspend fun getPictureOfDay(
        @Query("api_key") apiKey: String
    ): Response<PictureOfDay>
}