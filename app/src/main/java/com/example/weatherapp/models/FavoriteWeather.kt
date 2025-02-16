package com.example.weatherapp.models

import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "favorite_weather", primaryKeys = ["cityName", "country"])
data class FavoriteWeather(
    val cityName: String,
    val country: String,
    val temperature: Double,
    val description: String,
    val minTemp: Double,
    val maxTemp: Double,
    val feelsLike: Double,
    val windSpeed: Double,
    val iconCode: String,
    val lat: Double,
    val lon: Double
)
