package com.udacity.asteroidradar.api.data

import com.squareup.moshi.JsonClass
import com.squareup.moshi.JsonReader
import org.json.JSONObject

@JsonClass(generateAdapter = true)
data class AsteroidResponse(
    val element_count: Int,
    val links: Links,
    val near_earth_objects: Map<String, Object>
)