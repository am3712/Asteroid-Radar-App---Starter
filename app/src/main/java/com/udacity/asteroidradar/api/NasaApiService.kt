package com.udacity.asteroidradar.api

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.udacity.asteroidradar.Constants.API_KEY_QUERY_PARAM
import com.udacity.asteroidradar.Constants.API_KEY_QUERY_PARAM_VALUE
import com.udacity.asteroidradar.Constants.API_QUERY_DATE_FORMAT
import com.udacity.asteroidradar.Constants.ASTEROIDS_NEO_WS
import com.udacity.asteroidradar.Constants.ASTEROIDS_PICTURE_OF_DAY
import com.udacity.asteroidradar.Constants.BASE_URL
import com.udacity.asteroidradar.Constants.END_DATE_QUERY_PARAM
import com.udacity.asteroidradar.Constants.START_DATE_QUERY_PARAM
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(ScalarsConverterFactory.create())
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

interface NasaApiService {

    @GET(ASTEROIDS_NEO_WS)
    suspend fun getAsteroids(
        @Query(API_KEY_QUERY_PARAM) apiKey: String = API_KEY_QUERY_PARAM_VALUE,
        @Query(START_DATE_QUERY_PARAM) startDate: String? = null,
        @Query(END_DATE_QUERY_PARAM) endDate: String? = startDate?.let {
            val formatter = DateTimeFormatter.ofPattern(API_QUERY_DATE_FORMAT)
            LocalDate.parse(it, formatter).plusDays(7L).format(formatter)
        }
    ): NetworkNeoFeed

    @GET(ASTEROIDS_PICTURE_OF_DAY)
    suspend fun getPictureOfDay(@Query(API_KEY_QUERY_PARAM) apiKey: String = API_KEY_QUERY_PARAM_VALUE): NetworkAsteroidPictureOfDay
}


object Network {
    val nasaApiService: NasaApiService by lazy { retrofit.create(NasaApiService::class.java) }
}
