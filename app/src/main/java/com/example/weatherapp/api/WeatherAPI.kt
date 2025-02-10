package com.example.weatherapp.api

import com.example.weatherapp.models.WeatherResponse
import com.example.weatherapp.util.Constants.Companion.API_KEY
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherAPI {

    @GET("data/2.5/weather")
    suspend fun getWeatherData(
        @Query("lat")
        lat : Double,
        @Query("lon")
        lon : Double,
        @Query("units")
        units : String,
        @Query("appid")
        apiKey : String = API_KEY
    ) : Response<WeatherResponse>
}