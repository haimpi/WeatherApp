package com.example.weatherapp.ui

import android.Manifest
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.R
import com.example.weatherapp.models.WeatherResponse
import com.example.weatherapp.models.forecast.ForecastResponse
import com.example.weatherapp.repository.WeatherRepository
import com.example.weatherapp.util.Resource
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val repository: WeatherRepository,
    application: Application
): AndroidViewModel(application){

    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(application)

    val weatherData : MutableLiveData<Resource<WeatherResponse>> = MutableLiveData()

    val forecastData : MutableLiveData<Resource<ForecastResponse>> = MutableLiveData()

    private val _isLoading = MutableLiveData(true)
    val isLoading : LiveData<Boolean> get() = _isLoading

    //private val _isLoading = MutableStateFlow(true)
    //val isLoading = _isLoading.asStateFlow()

    init {
        viewModelScope.launch {
            delay(3000)
            _isLoading.value = false
        }
    }


    fun requestLocation(context: Context) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED) {
            weatherData.postValue(Resource.Error("Location permission not granted"))
            forecastData.postValue(Resource.Error("Location permission not granted"))
            return
        }

        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
            .addOnSuccessListener { location ->
                location?.let {
                    getWeather(it.latitude, it.longitude)
                    getForecast(it.latitude, it.longitude)}
                    ?: run{
                        weatherData.postValue(Resource.Error("Location unavailable"))
                        forecastData.postValue(Resource.Error("Location unavailable"))
                    }
            }
            .addOnFailureListener { exception ->
                weatherData.postValue(Resource.Error("Failed to get location: ${exception.message}"))
                forecastData.postValue(Resource.Error("Failed to get location: ${exception.message}"))
            }
    }

    fun getWeather(lat: Double, lon: Double, unit: String = "metric"){
        weatherData.value = Resource.Loading()

        viewModelScope.launch {
            try {
                val response = repository.getWeatherData(lat, lon, unit)

                if(response.isSuccessful){
                    response.body()?.let {
                        weatherData.postValue(Resource.Success(it))
                    } ?: run{
                        weatherData.postValue(Resource.Error("No data found"))
                    }
                }else{
                    weatherData.postValue(Resource.Error(response.message()))
                }
            }catch (e: Exception){
                weatherData.postValue(Resource.Error("No data found"))
            }
        }
    }

    fun getForecast(lat : Double, lon : Double, unit: String = "metric"){
        weatherData.value = Resource.Loading()

        viewModelScope.launch {
            try {
                val response = repository.getForecastData(lat, lon, unit)

                if(response.isSuccessful){
                    response.body()?.let {
                        forecastData.postValue(Resource.Success(it))
                    } ?: run{
                        forecastData.postValue(Resource.Error("No data found"))
                    }
                }else{
                    forecastData.postValue(Resource.Error(response.message()))
                }
            }catch (e: Exception){
                forecastData.postValue(Resource.Error("No data found"))
            }
        }
    }
}