package com.example.weatherapp.ui.fragments

//-------------arthur code----------

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.weatherapp.R
import com.example.weatherapp.databinding.FragmentCitySearchBinding
import com.example.weatherapp.ui.CitySearchViewModel
import com.example.weatherapp.util.Resource
import dagger.hilt.android.AndroidEntryPoint
import il.co.syntax.fullarchitectureretrofithiltkotlin.utils.autoCleared
import java.util.Locale

@AndroidEntryPoint
class CitySearchFragment : Fragment() {

    private var binding: FragmentCitySearchBinding by autoCleared()

    private val viewModel: CitySearchViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCitySearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSearch.setOnClickListener {
            val cityName = binding.etCityName.text.toString()
            val countryCode = binding.etCountryCode.text.toString()

            if (cityName.isNotEmpty() && countryCode.isNotEmpty()) {
                viewModel.getWeatherByCity(cityName, countryCode)
            } else {
                Toast.makeText(requireContext(), getString(R.string.please_enter_city_country), Toast.LENGTH_SHORT).show()            }
        }

        viewModel.weatherData.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Success -> {
                    val weather = resource.data
                    hideLoading()
                    with(binding) {
                        tvCity.text = getString(R.string.label_city, weather?.name ?: "Unknown")
                        tvCountry.text = getString(
                            R.string.label_country,
                            Locale("", weather?.sys?.country ?: "").displayCountry
                        )
                        tvTemperature.text = getString(R.string.label_temp, weather?.main?.temp ?: 0)
                        tvWindSpeed.text = getString(R.string.label_wind, weather?.wind?.speed ?: 0)

                    }
                }
                is Resource.Error -> {
                    hideLoading()
                    Toast.makeText(requireContext(), resource.message ?: "Error fetching weather", Toast.LENGTH_SHORT).show()
                }
                is Resource.Loading -> {
                    showLoading()
                }
            }
        }
    }

    private fun showLoading() {
        binding.tvCity.text = getString(R.string.loading)
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        binding.progressBar.visibility = View.GONE
    }
}
