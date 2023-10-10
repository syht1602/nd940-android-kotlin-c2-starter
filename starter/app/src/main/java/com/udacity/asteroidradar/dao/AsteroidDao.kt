package com.udacity.asteroidradar.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.udacity.asteroidradar.domain.models.Asteroid

@Dao
interface AsteroidDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(asteroids: List<Asteroid>)

    @Query("DELETE FROM asteroids WHERE closeApproachDate = :date")
    suspend fun delete(date: String)

    @Query("SELECT * FROM asteroids ORDER BY closeApproachDate DESC")
    suspend fun getAllAsteroids(): List<Asteroid>

    @Query("SELECT * FROM asteroids WHERE closeApproachDate = :date ORDER BY closeApproachDate DESC")
    fun getSpecificDayAsteroid(date: String): List<Asteroid>

    @Query("SELECT * FROM asteroids WHERE (closeApproachDate BETWEEN :startDate AND :endDate) ORDER BY closeApproachDate DESC")
    fun getWeekAsteroid(startDate: String, endDate: String): List<Asteroid>
}
