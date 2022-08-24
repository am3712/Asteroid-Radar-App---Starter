package com.udacity.asteroidradar.main

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.databinding.FragmentMainBinding

class MainFragment : Fragment() {

    private val viewModel: MainViewModel by viewModels { MainViewModel.Factory(requireActivity().application) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentMainBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        // Sets the adapter of the photosGrid RecyclerView with clickHandler lambda that
        // tells the viewModel when our property is clicked
        binding.asteroidRecycler.adapter =
            AsteroidAdapter(AsteroidListener { viewModel.displayAsteroidDetails(it) })

        // Observe the navigateToAsteroidDetails LiveData and Navigate when it isn't null
        // After navigating, call displayPropertyDetailsComplete() so that the ViewModel is ready
        // for another navigation event.
        viewModel.navigateToAsteroidDetails.observe(viewLifecycleOwner) {
            if (null != it) {
                // Must find the NavController from the Fragment
                this.findNavController().navigate(MainFragmentDirections.actionShowDetail(it))
                // Tell the ViewModel we've made the navigate call to prevent multiple navigation
                viewModel.displayPropertyDetailsComplete()
            }
        }

        // Observe the showSnackBarEvent LiveData and show a Snackbar
        viewModel.showSnackBarEvent.observe(viewLifecycleOwner) { message ->
            if (null != message) {
                Snackbar.make(
                    requireActivity().findViewById(android.R.id.content),
                    message,
                    Snackbar.LENGTH_LONG // How long to display the message.
                ).setAction(R.string.retry) { viewModel.refresh() }.show()
                // Reset state to make sure the snackbar is only shown once, even if the device
                // has a configuration change.
                viewModel.doneShowingSnackbar()
            }
        }


        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_overflow_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    /**
     * Updates the view type in the [MainViewModel] when the menu items are selected from the
     * overflow menu.
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        viewModel.updateView(
            when (item.itemId) {
                R.id.show_today_asteroids -> AsteroidViewType.VIEW_TODAY_ASTEROIDS
                R.id.show_saved_asteroids -> AsteroidViewType.VIEW_SAVED_ASTEROIDS
                else -> AsteroidViewType.VIEW_WEEK_ASTEROIDS
            }
        )
        return true
    }
}
