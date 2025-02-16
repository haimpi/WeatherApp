package com.example.weatherapp.ui.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.weatherapp.R
import com.example.weatherapp.adapters.ForecastAdapter
import com.example.weatherapp.databinding.FragmentWeatherLocalBinding
import com.example.weatherapp.ui.WeatherViewModel
import com.example.weatherapp.util.Resource
import com.example.weatherapp.util.WeatherIconProvider
import dagger.hilt.android.AndroidEntryPoint
import il.co.syntax.fullarchitectureretrofithiltkotlin.utils.autoCleared
import java.util.Locale

@AndroidEntryPoint
class WeatherLocalFragment : Fragment() {

    private var binding: FragmentWeatherLocalBinding by autoCleared()

    private val viewModel: WeatherViewModel by viewModels()

    private lateinit var forecastAdatper : ForecastAdapter

    private val locationPermissionRequest =
        registerForActivityResult(ActivityResultContracts.RequestPermission()){isGranted->
            if(isGranted) viewModel.requestLocation(requireContext())
            else binding.tvCity.text = "Location Permission denied" //TODO
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWeatherLocalBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpRecycleView()
        checkLocationPermission()

        viewModel.weatherData.observe(viewLifecycleOwner) { resource ->
            when(resource){
                is Resource.Success ->{
                    hideProgressBar()
                    val weather = resource.data
                    val iconCode = weather?.weather?.get(0)?.icon
                    val iconResult = WeatherIconProvider.getWeatherIcon(iconCode)

                    with(binding) {
                        tvCity.text = weather?.name ?: "Unknown City"
                        tvTemperature.text = String.format(Locale.US, "%.0f°C", weather?.main?.temp ?: 0f)
                        tvDescriptionWeather.text = weather?.weather?.get(0)?.description ?: "No description available"
                        tvMinTemperature.text = String.format(Locale.US, "Min: %.0f°C", weather?.main?.temp_min ?: 0f)
                        tvMaxTemperature.text = String.format(Locale.US, "Max: %.0f°C", weather?.main?.temp_max ?: 0f)
                        tvFeelsLike.text = String.format(Locale.US, "Feels like: %.0f°C", weather?.main?.feels_like ?: 0f)
                        tvWind.text = String.format(Locale.US, "Wind: %.1f m/s", weather?.wind?.speed ?: 0f)
                        ivIconWeather.setImageResource(iconResult) //todo glide
                    }
                }
                is Resource.Error ->{
                    hideProgressBar()
                }
                is Resource.Loading ->{
                    showProgressBar()
                }
            }
        }

        viewModel.forecastData.observe(viewLifecycleOwner){resource->
            when(resource){
                is Resource.Success ->{
                    hideProgressBar()
                    resource.data?.list?.let { forecastList->
                        forecastAdatper.differ.submitList(forecastList)
                    }
                }
                is Resource.Error ->{
                    hideProgressBar()
                }
                is Resource.Loading ->{
                    showProgressBar()
                }
            }
        }
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            if (viewModel.weatherData.value == null) { // Prevent duplicate requests
                viewModel.requestLocation(requireContext())
            }
        } else {
            locationPermissionRequest.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun hideProgressBar(){
        binding.paginationProgressBar.visibility = View.INVISIBLE
    }

    private fun showProgressBar(){
        binding.paginationProgressBar.visibility = View.VISIBLE
    }

    private fun setUpRecycleView(){
        forecastAdatper = ForecastAdapter()
        binding.recycleView.apply {
            adapter = forecastAdatper
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
    }
}