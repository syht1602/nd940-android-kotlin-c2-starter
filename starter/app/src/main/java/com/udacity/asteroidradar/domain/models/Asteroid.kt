package com.udacity.asteroidradar.domain.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "asteroids")
@Parcelize
data class Asteroid(
    @PrimaryKey
    var id: Long = -1,
    var codename: String = "",
    var closeApproachDate: String = "",
    var absoluteMagnitude: Double = 0.0,
    var estimatedDiameter: Double = 0.0,
    var relativeVelocity: Double = 0.0,
    var distanceFromEarth: Double = 0.0,
    var isPotentiallyHazardous: Boolean = false
) : Parcelable