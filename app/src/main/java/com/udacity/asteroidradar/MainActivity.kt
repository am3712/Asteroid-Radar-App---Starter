package com.udacity.asteroidradar

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import timber.log.Timber
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val date = LocalDate.now()
        val localDateLongValue = date?.atStartOfDay(ZoneOffset.UTC)?.toEpochSecond()
        val conversionDate = localDateLongValue?.let {
            Instant.ofEpochSecond(it).atZone(ZoneOffset.UTC).toLocalDate()
        }
        Timber.d("date: $date")
        Timber.d("conversionDate: $conversionDate")
    }
}
