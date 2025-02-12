package com.example.weatherapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.weatherapp.R
import com.example.weatherapp.databinding.FragmentCitySearchBinding
import com.example.weatherapp.models.WeatherResponse
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

        val countryAdapter = ArrayAdapter(requireContext(), R.layout.spinner_item, mutableListOf<String>())
        binding.spinnerCountry.adapter = countryAdapter

        val cityAdapter = ArrayAdapter(requireContext(), R.layout.spinner_item, mutableListOf<String>())
        binding.spinnerCity.adapter = cityAdapter
        binding.spinnerCity.isEnabled = false



        viewModel.selectedCountry.observe(viewLifecycleOwner) { selected ->
            val index = countryAdapter.getPosition(selected)
            if (index != -1) binding.spinnerCountry.setSelection(index)
        }

        viewModel.selectedCity.observe(viewLifecycleOwner) { selected ->
            val index = cityAdapter.getPosition(selected)
            if (index != -1) binding.spinnerCity.setSelection(index)
        }


        viewModel.countryList.observe(viewLifecycleOwner) { countries ->
            countryAdapter.clear()
            countryAdapter.addAll(countries)
            countryAdapter.notifyDataSetChanged()
        }

        viewModel.cityList.observe(viewLifecycleOwner) { cities ->
            cityAdapter.clear()
            cityAdapter.addAll(cities)
            cityAdapter.notifyDataSetChanged()
            binding.spinnerCity.isEnabled = cities.isNotEmpty()
        }

        viewModel.weatherData.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Success -> {
                    hideLoading()
                    updateUI(resource.data!!)
                }
                is Resource.Error -> {
                    hideLoading()
                    showError(resource.message ?: getString(R.string.error_fetch_weather))
                }
                is Resource.Loading -> showLoading()
            }
        }

        binding.spinnerCountry.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedCountry = parent?.getItemAtPosition(position) as? String ?: return
                viewModel.selectedCountry.value = selectedCountry
                viewModel.getCitiesForCountry(selectedCountry)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }


        binding.btnSearch.setOnClickListener {
            val selectedCountry = binding.spinnerCountry.selectedItem as? String ?: ""
            val selectedCity = binding.spinnerCity.selectedItem as? String ?: ""

            if (selectedCountry.isNotEmpty() && selectedCity.isNotEmpty()) {
                viewModel.selectedCity.value = selectedCity
                viewModel.getWeatherByCity(selectedCity, selectedCountry)
            } else {
                Toast.makeText(requireContext(), getString(R.string.enter_city_name), Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun updateUI(weather: WeatherResponse) {
        with(binding) {
            tvCity.text = getString(R.string.label_city, viewModel.selectedCity.value ?: "Unknown")
            tvCountry.text = getString(R.string.label_country, viewModel.selectedCountry.value ?: "Unknown")
            tvTemperature.text = getString(R.string.label_temp, weather.main.temp ?: 0)
            tvWindSpeed.text = getString(R.string.label_wind, weather.wind.speed ?: 0)
        }
    }

    private fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
        with(binding) {
            tvCity.text = getString(R.string.error_no_data)
            tvTemperature.text = ""
            tvWindSpeed.text = ""
        }
    }
    override fun onResume() {
        super.onResume()
        viewModel.refreshData(requireContext())

    }

    private fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        binding.progressBar.visibility = View.GONE
    }
}
