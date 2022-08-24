package com.udacity.asteroidradar.main

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.*
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.repository.AsteroidsRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber


class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val database = getDatabase(application)
    private val asteroidsRepository = AsteroidsRepository(database)

    private val _pictureOfDay = MutableLiveData<PictureOfDay>()
    val pictureOfDay: LiveData<PictureOfDay>
        get() = _pictureOfDay


    // The internal MutableLiveData that stores the status of the list view
    private val viewType = MutableLiveData(AsteroidViewType.VIEW_TODAY_ASTEROIDS)

    private val _showSnackBarEvent = MutableLiveData<String>()
    val showSnackBarEvent: LiveData<String>
        get() = _showSnackBarEvent

    fun doneShowingSnackbar() {
        _showSnackBarEvent.value = null
    }

    private val _loadingStatus = MutableLiveData<Boolean>()
    val loadingStatus: LiveData<Boolean>
        get() = _loadingStatus


    val asteroidList = Transformations.switchMap(viewType) { type ->
        // start loading
        _loadingStatus.value = true
        when (type) {
            AsteroidViewType.VIEW_WEEK_ASTEROIDS -> asteroidsRepository.weekAsteroids
            AsteroidViewType.VIEW_TODAY_ASTEROIDS -> asteroidsRepository.todayAsteroids
            AsteroidViewType.VIEW_SAVED_ASTEROIDS -> asteroidsRepository.allAsteroids
        }
    }

    // boolean state used to make sure we try load data only once when list is empty
    var alreadyTryToRefreshAsteroids: Boolean = false
    val isListEmpty = Transformations.map(asteroidList) {
        // stop loading
        _loadingStatus.value = false
        val isListEmpty = it.isEmpty()
        if (!alreadyTryToRefreshAsteroids && isListEmpty)
            refreshAsteroidsData()
        isListEmpty
    }

    private fun refreshAsteroidsData() {
        Timber.i("refreshAsteroidsData()")
        launchDataLoad {
            asteroidsRepository.refreshAsteroids()
            alreadyTryToRefreshAsteroids = true
        }
    }

    // Internally, we use a MutableLiveData to handle navigation to the selected asteroid
    private val _navigateToAsteroidDetails = MutableLiveData<Asteroid>()

    // The external immutable LiveData for the navigation asteroid
    val navigateToAsteroidDetails: LiveData<Asteroid>
        get() = _navigateToAsteroidDetails

    /**
     * When the asteroid is clicked, set the [_navigateToAsteroidDetails] [MutableLiveData]
     * @param asteroid The [Asteroid] that was clicked on.
     */
    fun displayAsteroidDetails(asteroid: Asteroid) {
        _navigateToAsteroidDetails.value = asteroid
    }

    /**
     * After the navigation has taken place, make sure navigateToAsteroidDetails is set to null
     */
    @SuppressLint("NullSafeMutableLiveData")
    fun displayPropertyDetailsComplete() {
        _navigateToAsteroidDetails.value = null
    }

    init {
        loadPictureOfDay()
    }

    fun updateView(requestType: AsteroidViewType) {
        val oldType = viewType.value
        if (oldType != requestType)
            viewType.value = requestType
    }

    fun refresh() {
        Timber.i("refresh()")
        if (_pictureOfDay.value == null)
            loadPictureOfDay()
        else
            refreshAsteroidsData()
    }

    private fun loadPictureOfDay() {
        Timber.i("loadPictureOfDay()")
        launchDataLoad {
            _pictureOfDay.value = asteroidsRepository.loadPictureOfDay()
        }
    }

    private fun launchDataLoad(block: suspend () -> Unit): Job {
        return viewModelScope.launch {
            try {
                _loadingStatus.value = true
                block()
            } catch (error: Exception) {
                _showSnackBarEvent.value = error.message
            } finally {
                _loadingStatus.value = false
            }
        }
    }


    /**
     * Factory for constructing MainViewModel with parameter
     */
    class Factory(val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MainViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct viewModel")
        }
    }
}