package com.example.weatherapp.repository

import com.example.weatherapp.api.RetrofitInstance
import com.example.weatherapp.api.WeatherAPI
import com.example.weatherapp.db.WeatherDatabase
import com.example.weatherapp.models.WeatherResponse
import javax.inject.Inject

class WeatherRepository @Inject constructor(
    private val api: WeatherAPI,
    private val db : WeatherDatabase
) {

    suspend fun getWeatherData(lat: Double, lon: Double, unit: String) =
        api.getWeatherData(lat,lon,unit)

    suspend fun getForecastData(lat: Double, lon: Double, unit: String) =
        api.getForecastData(lat,lon,unit)

    suspend fun upsertWeatherData(weatherResponse: WeatherResponse) =
        db.getWeatherDao().upsertWeatherData(weatherResponse)

    fun getSavedWeather() = db.getWeatherDao().getAllWeatherData()

    suspend fun deleteWeather(weatherResponse: WeatherResponse) =
        db.getWeatherDao().deleteWeatherData(weatherResponse)
}
