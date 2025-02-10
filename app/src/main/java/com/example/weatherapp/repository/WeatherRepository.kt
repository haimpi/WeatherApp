package com.example.weatherapp.repository

import com.example.weatherapp.api.RetrofitInstance
import com.example.weatherapp.api.WeatherAPI
import com.example.weatherapp.db.WeatherDatabase
import com.example.weatherapp.models.CityResponse
import com.example.weatherapp.models.WeatherResponse
import javax.inject.Inject

class WeatherRepository @Inject constructor(
    private val api: WeatherAPI,
    private val db : WeatherDatabase
) {
    //---------------arthur code------------------
    suspend fun getCoordinates(city: String, country: String) =
        api.getCoordinates("$city,$country")
    //--------------------------------------------

    suspend fun getWeatherData(lat: Double, lon: Double, unit: String) =
        api.getWeatherData(lat,lon,unit)

    suspend fun insertWeatherData(weatherResponse: WeatherResponse) =
        db.getWeatherDao().upsertWeatherData(weatherResponse)

    fun getSavedWeather() = db.getWeatherDao().getAllWeatherData()

    suspend fun deleteWeather(weatherResponse: WeatherResponse) =
        db.getWeatherDao().deleteWeatherData(weatherResponse)
}
