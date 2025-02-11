package com.example.weatherapp.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.R
import com.example.weatherapp.models.WeatherResponse
import com.example.weatherapp.repository.WeatherRepository
import com.example.weatherapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CitySearchViewModel @Inject constructor(
    private val repository: WeatherRepository,
    private val app: Application
) : AndroidViewModel(app) {

    val weatherData: MutableLiveData<Resource<WeatherResponse>> = MutableLiveData()

    fun getWeatherByCity(city: String, country: String) {
        weatherData.value = Resource.Loading()

        viewModelScope.launch {
            val response = repository.getCoordinates(city, country)

            if (response.isSuccessful) {
                val cityList = response.body()
                val firstCity = cityList?.firstOrNull()

                firstCity?.let {
                    val weatherResponse = repository.getWeatherData(it.lat, it.lon, "metric")
                    handleWeatherResponse(weatherResponse)
                } ?: weatherData.postValue(Resource.Error(app.getString(R.string.error_city_not_found)))
            } else {
                weatherData.postValue(Resource.Error(app.getString(R.string.error_fetch_coordinates)))
            }
        }
    }

    private fun handleWeatherResponse(response: retrofit2.Response<WeatherResponse>) {
        if (response.isSuccessful) {
            response.body()?.let {
                weatherData.postValue(Resource.Success(it))
            } ?: weatherData.postValue(Resource.Error(app.getString(R.string.error_no_data)))
        } else {
            weatherData.postValue(Resource.Error(app.getString(R.string.error_fetch_weather)))
        }
    }
}


