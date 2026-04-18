package com.univ.routeplanner

import android.Manifest
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.univ.routeplanner.databinding.ActivityMainBinding
import com.univ.routeplanner.ui.MainViewModel
import com.univ.routeplanner.ui.RouteUiState
import com.univ.routeplanner.util.LocationHelper
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var locationHelper: LocationHelper

    private val viewModel: MainViewModel by viewModels {
        MainViewModel.Factory(applicationContext)
    }

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) fetchLocation()
        else binding.tvStatus.text = "Location permission denied."
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        locationHelper = LocationHelper(this)

        setupObservers()
        setupClickListeners()
        viewModel.loadLastCachedRoute()
    }

    private fun setupClickListeners() {
        binding.btnGetLocation.setOnClickListener {
            if (locationHelper.hasLocationPermission()) fetchLocation()
            else permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        binding.btnGetRoute.setOnClickListener {
            val destination = binding.etDestination.text?.toString()?.trim().orEmpty()
            viewModel.fetchRoute(destination)
        }
    }

    private fun setupObservers() {
        viewModel.currentLocation.observe(this) { coords ->
            binding.tvCurrentLocation.text = coords ?: "Tap the button to get your location"
        }

        viewModel.uiState.observe(this) { state ->
            renderState(state)
        }
    }

    private fun renderState(state: RouteUiState) {
        when (state) {
            is RouteUiState.Idle -> {
                binding.progressBar.visibility = View.GONE
                binding.tvStatus.text = ""
                binding.tvDistance.text = "Distance: —"
                binding.tvDuration.text = "Duration: —"
                binding.tvSource.text = ""
            }
            is RouteUiState.Loading -> {
                binding.progressBar.visibility = View.VISIBLE
                binding.tvStatus.text = ""
                binding.tvDistance.text = "Distance: ..."
                binding.tvDuration.text = "Duration: ..."
                binding.tvSource.text = ""
            }
            is RouteUiState.Success -> {
                binding.progressBar.visibility = View.GONE
                binding.tvStatus.text = ""
                binding.tvDistance.text = "Distance: %.2f km".format(state.distanceKm)
                binding.tvDuration.text = "Duration: %.1f min".format(state.durationMin)
                binding.tvSource.text = "Source: ${state.source}"
            }
            is RouteUiState.OfflineFallback -> {
                binding.progressBar.visibility = View.GONE
                binding.tvStatus.text = state.message
                binding.tvDistance.text = "Distance: %.2f km".format(state.distanceKm)
                binding.tvDuration.text = "Duration: %.1f min".format(state.durationMin)
                binding.tvSource.text = "Source: offline cache"
            }
            is RouteUiState.Error -> {
                binding.progressBar.visibility = View.GONE
                binding.tvStatus.text = state.message
                binding.tvDistance.text = "Distance: —"
                binding.tvDuration.text = "Duration: —"
                binding.tvSource.text = ""
            }
        }
    }

    private fun fetchLocation() {
        binding.tvStatus.text = ""
        binding.tvCurrentLocation.text = "Getting location..."

        lifecycleScope.launch {
            try {
                val location = locationHelper.getCurrentLocation()
                if (location != null) {
                    val coords = "${location.longitude},${location.latitude}"
                    viewModel.setCurrentLocation(coords)
                } else {
                    binding.tvCurrentLocation.text = "Could not determine location"
                }
            } catch (e: Exception) {
                binding.tvStatus.text = "Error: ${e.message}"
            }
        }
    }
}