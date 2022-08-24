package com.udacity.asteroidradar.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.Constants.API_QUERY_DATE_FORMAT
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

@Entity(tableName = "asteroid")
data class DatabaseAsteroid(
    @PrimaryKey val id: Long,
    val codename: String,
    val closeApproachDate: LocalDate,
    val absoluteMagnitude: Double,
    val estimatedDiameter: Double,
    val relativeVelocity: Double,
    val distanceFromEarth: Double,
    val isPotentiallyHazardous: Boolean
)

fun List<DatabaseAsteroid>.asDomainModel(): List<Asteroid> {
    val formatter = DateTimeFormatter.ofPattern(API_QUERY_DATE_FORMAT, Locale.ENGLISH)
    return map {
        it.run {
            Asteroid(
                id = id,
                codename = codename,
                closeApproachDate = closeApproachDate.format(formatter),
                absoluteMagnitude = absoluteMagnitude,
                estimatedDiameter = estimatedDiameter,
                relativeVelocity = relativeVelocity,
                distanceFromEarth = distanceFromEarth,
                isPotentiallyHazardous = isPotentiallyHazardous
            )
        }
    }
}
