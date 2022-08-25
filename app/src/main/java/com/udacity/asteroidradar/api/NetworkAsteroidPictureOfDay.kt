package com.udacity.asteroidradar.api


import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.udacity.asteroidradar.PictureOfDay

@Keep
@JsonClass(generateAdapter = true)
data class NetworkAsteroidPictureOfDay(
    @Json(name = "copyright") val copyright: String?,
    @Json(name = "date") val date: String?,
    @Json(name = "explanation") val explanation: String?,
    @Json(name = "hdurl") val hdUrl: String?,
    @Json(name = "media_type") val mediaType: String?,
    @Json(name = "service_version") val serviceVersion: String?,
    @Json(name = "title") val title: String?,
    @Json(name = "url") val url: String?
)


fun NetworkAsteroidPictureOfDay.asDomainModel() = PictureOfDay(
    url = url.orEmpty(),
    title = title.orEmpty(),
    mediaType = mediaType.orEmpty()
)