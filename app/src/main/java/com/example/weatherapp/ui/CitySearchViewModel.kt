package com.example.weatherapp.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.R
import com.example.weatherapp.models.WeatherResponse
import com.example.weatherapp.repository.WeatherRepository
import com.example.weatherapp.util.Resource
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class CitySearchViewModel @Inject constructor(
    private val repository: WeatherRepository,
    private val app: Application
) : AndroidViewModel(app) {

    val weatherData: MutableLiveData<Resource<WeatherResponse>> = MutableLiveData()
    val countryList: MutableLiveData<List<String>> = MutableLiveData()
    val cityList: MutableLiveData<List<String>> = MutableLiveData()
    val selectedCountry: MutableLiveData<String> = MutableLiveData()
    val selectedCity: MutableLiveData<String> = MutableLiveData()

    private var citiesMap: Map<String, List<String>> = emptyMap()

    init {
        loadCityList(app.applicationContext)
    }

    private fun loadCityList(context: Context) {
        viewModelScope.launch {
            try {
                val json = context.assets.open("cities.json").bufferedReader().use { it.readText() }
                citiesMap = Gson().fromJson(json, object : TypeToken<Map<String, List<String>>>() {}.type)
                countryList.postValue(citiesMap.keys.toList())
            } catch (e: Exception) {
                countryList.postValue(emptyList())
            }
        }
    }

    fun getCitiesForCountry(selectedCountry: String) {
        this.selectedCountry.postValue(selectedCountry)
        val cities = citiesMap[selectedCountry] ?: emptyList()
        cityList.postValue(cities)

        selectedCity.value?.let { savedCity ->
            if (savedCity in cities) selectedCity.postValue(savedCity)
        }
    }

    fun getWeatherByCity(city: String, country: String) {
        selectedCity.postValue(city)
        weatherData.postValue(Resource.Loading())

        viewModelScope.launch {
            try {
                val response = repository.getCoordinates(city, country)
                if (response.isSuccessful) {
                    response.body()?.firstOrNull()?.let {
                        val weatherResponse = repository.getWeatherData(it.lat, it.lon, "metric")
                        handleWeatherResponse(weatherResponse)
                    } ?: weatherData.postValue(Resource.Error(app.getString(R.string.error_city_not_found)))
                } else {
                    weatherData.postValue(Resource.Error(app.getString(R.string.error_fetch_coordinates)))
                }
            } catch (e: Exception) {
                weatherData.postValue(Resource.Error(app.getString(R.string.error_fetch_coordinates) + ": " + e.message))
            }
        }
    }

    private fun handleWeatherResponse(response: Response<WeatherResponse>) {
        if (response.isSuccessful) {
            response.body()?.let {
                weatherData.postValue(Resource.Success(it))
            } ?: weatherData.postValue(Resource.Error(app.getString(R.string.error_no_data)))
        } else {
            weatherData.postValue(Resource.Error(app.getString(R.string.error_fetch_weather) + ": " + response.message()))
        }
    }

    fun refreshData(context: Context) {
        val currentCountry = selectedCountry.value
        val currentCity = selectedCity.value
        loadCityList(context)
        selectedCountry.postValue(currentCountry ?: citiesMap.keys.firstOrNull())
        selectedCity.postValue(currentCity ?: citiesMap[selectedCountry.value]?.firstOrNull())
    }
}


