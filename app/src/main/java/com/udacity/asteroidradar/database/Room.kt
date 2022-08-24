package com.udacity.asteroidradar.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*
import java.time.LocalDate

@Dao
interface AsteroidDao {
    @Query("select * from asteroid WHERE closeApproachDate = :ofDay ORDER BY closeApproachDate")
    fun getAsteroidsOfToday(ofDay: LocalDate = LocalDate.now()): LiveData<List<DatabaseAsteroid>>

    @Query("select * from asteroid WHERE closeApproachDate BETWEEN :startDay AND :endDay ORDER BY closeApproachDate;")
    fun getAsteroidsOfWeek(
        startDay: LocalDate = LocalDate.now(),
        endDay: LocalDate = startDay.plusDays(7)
    ): LiveData<List<DatabaseAsteroid>>

    @Query("select * from asteroid ORDER BY closeApproachDate;")
    fun getAsteroids(): LiveData<List<DatabaseAsteroid>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg videos: DatabaseAsteroid)

    @Query("delete from asteroid where closeApproachDate < :ofDay;")
    suspend fun deleteBefore(ofDay: LocalDate = LocalDate.now())
}

@Database(entities = [DatabaseAsteroid::class], version = 1)
@TypeConverters(Converters::class)
abstract class AsteroidsDatabase : RoomDatabase() {
    abstract val asteroidDao: AsteroidDao
}

private lateinit var INSTANCE: AsteroidsDatabase

fun getDatabase(context: Context): AsteroidsDatabase {
    synchronized(AsteroidsDatabase::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(
                context.applicationContext,
                AsteroidsDatabase::class.java,
                "asteroids"
            ).fallbackToDestructiveMigration().build()
        }
    }
    return INSTANCE
}
