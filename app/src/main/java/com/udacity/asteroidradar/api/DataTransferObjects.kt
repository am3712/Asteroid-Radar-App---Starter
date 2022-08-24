package com.udacity.asteroidradar.api

import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.database.DatabaseAsteroid
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

/**
 * DataTransferObjects go in this file. These are responsible for parsing responses from the server
 * or formatting objects to send to the server. You should convert these to domain objects before
 * using them.
 */


@JsonClass(generateAdapter = true)
data class NetworkNeoFeed(
    @Json(name = "element_count") val elementCount: Int,
    @Json(name = "links") val links: Links,
    @Json(name = "near_earth_objects") val nearEarthObjects: Map<String, List<NearEarthObject>>
) {
    @JsonClass(generateAdapter = true)
    data class Links(
        @Json(name = "next") val next: String,
        @Json(name = "prev") val prev: String,
        @Json(name = "self") val self: String
    )

    @Keep
    @JsonClass(generateAdapter = true)
    data class NearEarthObject(
        @Json(name = "id") val id: String,
        @Json(name = "absolute_magnitude_h") val absoluteMagnitudeH: Double,
        @Json(name = "close_approach_data") val closeApproachData: List<CloseApproachData>,
        @Json(name = "estimated_diameter") val estimatedDiameter: EstimatedDiameter,
        @Json(name = "is_potentially_hazardous_asteroid") val isPotentiallyHazardousAsteroid: Boolean,
        @Json(name = "is_sentry_object") val isSentryObject: Boolean,
        @Json(name = "name") val name: String,
        @Json(name = "nasa_jpl_url") val nasaJplUrl: String,
        @Json(name = "neo_reference_id") val neoReferenceId: String
    ) {
        @JsonClass(generateAdapter = true)
        data class CloseApproachData(
            @Json(name = "close_approach_date") val closeApproachDate: String,
            @Json(name = "close_approach_date_full") val closeApproachDateFull: String,
            @Json(name = "epoch_date_close_approach") val epochDateCloseApproach: Long,
            @Json(name = "miss_distance") val missDistance: MissDistance,
            @Json(name = "orbiting_body") val orbitingBody: String,
            @Json(name = "relative_velocity") val relativeVelocity: RelativeVelocity
        ) {
            @JsonClass(generateAdapter = true)
            data class MissDistance(
                @Json(name = "astronomical") val astronomical: String,
                @Json(name = "kilometers") val kilometers: String,
                @Json(name = "lunar") val lunar: String,
                @Json(name = "miles") val miles: String
            )


            @JsonClass(generateAdapter = true)
            data class RelativeVelocity(
                @Json(name = "kilometers_per_hour") val kilometersPerHour: String,
                @Json(name = "kilometers_per_second") val kilometersPerSecond: String,
                @Json(name = "miles_per_hour") val milesPerHour: String
            )
        }


        @JsonClass(generateAdapter = true)
        data class EstimatedDiameter(@Json(name = "kilometers") val kilometers: Kilometers) {
            @Keep
            @JsonClass(generateAdapter = true)
            data class Kilometers(
                @Json(name = "estimated_diameter_max") val estimatedDiameterMax: Double,
                @Json(name = "estimated_diameter_min") val estimatedDiameterMin: Double
            )
        }

    }
}

/***
 *
 * val formatter = DateTimeFormatter.ofPattern(API_QUERY_DATE_FORMAT)
 * it.key == LocalDate.now().format(formatter)
 */


/**
 * Convert Network results to database objects
 */
fun NetworkNeoFeed.asDomainModel(): List<Asteroid> {
    return nearEarthObjects.values.flatten().map { nearEarthObject ->
        val closeApproachData = nearEarthObject.closeApproachData.first()
        Asteroid(
            id = nearEarthObject.id.toLong(),
            codename = nearEarthObject.name,
            closeApproachDate = closeApproachData.closeApproachDate,
            absoluteMagnitude = nearEarthObject.absoluteMagnitudeH,
            estimatedDiameter = nearEarthObject.estimatedDiameter.kilometers.estimatedDiameterMax,
            relativeVelocity = closeApproachData.relativeVelocity.kilometersPerSecond.toDouble(),
            distanceFromEarth = closeApproachData.missDistance.astronomical.toDouble(),
            isPotentiallyHazardous = nearEarthObject.isPotentiallyHazardousAsteroid
        )
    }
}

fun NetworkNeoFeed.asDatabaseModel(): Array<DatabaseAsteroid> {
    return nearEarthObjects.values.flatten().map { nearEarthObject ->
        val closeApproachData = nearEarthObject.closeApproachData.first()
        DatabaseAsteroid(
            id = nearEarthObject.id.toLong(),
            codename = nearEarthObject.name,
            closeApproachDate = LocalDate.parse(closeApproachData.closeApproachDate),
            absoluteMagnitude = nearEarthObject.absoluteMagnitudeH,
            estimatedDiameter = nearEarthObject.estimatedDiameter.kilometers.estimatedDiameterMax,
            relativeVelocity = closeApproachData.relativeVelocity.kilometersPerSecond.toDouble(),
            distanceFromEarth = closeApproachData.missDistance.astronomical.toDouble(),
            isPotentiallyHazardous = nearEarthObject.isPotentiallyHazardousAsteroid
        )
    }.toTypedArray()
}
