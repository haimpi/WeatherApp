package com.example.weatherapp.util

import com.example.weatherapp.R

object WeatherIconProvider {
    fun getWeatherIcon(iconCode: String?) : Int{
        val iconPrefix = iconCode?.substring(0,2)
        return when(iconPrefix){
            "01" -> R.drawable.ic_sunny
            "02" -> R.drawable.ic_sunnycloudy
            "03" -> R.drawable.ic_cloudy
            "04" -> R.drawable.ic_very_cloudy
            "09" -> R.drawable.ic_rainshower
            "10" -> R.drawable.ic_rainy
            "11" -> R.drawable.ic_thunder
            "13" -> R.drawable.ic_snowy
            "50" -> R.drawable.ic_pressure
            else -> R.drawable.ic_launcher_foreground
        }
    }
}