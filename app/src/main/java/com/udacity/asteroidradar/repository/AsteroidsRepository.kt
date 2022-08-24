package com.udacity.asteroidradar.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.api.Network
import com.udacity.asteroidradar.api.asDatabaseModel
import com.udacity.asteroidradar.api.asDomainModel
import com.udacity.asteroidradar.database.AsteroidsDatabase
import com.udacity.asteroidradar.database.asDomainModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AsteroidsRepository(private val database: AsteroidsDatabase) {

    /**
     * A list of today asteroids that can be shown on the screen.
     */
    val todayAsteroids: LiveData<List<Asteroid>> =
        Transformations.map(database.asteroidDao.getAsteroidsOfToday()) { it.asDomainModel() }

    /**
     * A list of week asteroids that can be shown on the screen.
     */
    val weekAsteroids: LiveData<List<Asteroid>> =
        Transformations.map(database.asteroidDao.getAsteroidsOfWeek()) { it.asDomainModel() }

    /**
     * A list of all saved asteroids that can be shown on the screen.
     */
    val allAsteroids: LiveData<List<Asteroid>> =
        Transformations.map(database.asteroidDao.getAsteroids()) { it.asDomainModel() }

    /**
     * Refresh the Asteroids stored in the offline cache.
     *
     * This function uses the IO dispatcher to ensure the database insert database operation
     * happens on the IO dispatcher. By switching to the IO dispatcher using `withContext` this
     * function is now safe to call from any thread including the Main thread.
     *
     * To actually load the Asteroids for use, observe [todayAsteroids]
     */
    suspend fun refreshAsteroids() {
        withContext(Dispatchers.IO) {
            val apiAsteroids = Network.nasaApiService.getAsteroids()
            database.asteroidDao.insertAll(*apiAsteroids.asDatabaseModel())
        }
    }

    suspend fun loadPictureOfDay() =
        withContext(Dispatchers.IO) {
            Network.nasaApiService.getPictureOfDay().asDomainModel()
        }

    suspend fun removeAsteroidsBeforeToday() {
        withContext(Dispatchers.IO) {
            database.asteroidDao.deleteBefore()
        }
    }
}
