package com.example.weatherapp.repository

import com.example.weatherapp.api.WeatherAPI
import com.example.weatherapp.db.WeatherDatabase
import com.example.weatherapp.models.CityResponse
import com.example.weatherapp.models.FavoriteWeather
import com.example.weatherapp.models.WeatherResponse
import retrofit2.Response
import javax.inject.Inject

class WeatherRepository @Inject constructor(
    private val api: WeatherAPI,
    private val db : WeatherDatabase
) {
    //---------------arthur code------------------
    suspend fun getCoordinates(city: String, country: String? = null): Response<List<CityResponse>> {
        val query = if (country.isNullOrEmpty()) city else "$city,$country"
        return api.getCoordinates(query)
    }

    suspend fun getCitiesFromAPI(query: String): Response<List<CityResponse>> {
        return api.searchCities(query)
    }


// ------------------- ניהול מועדפים -------------------

    suspend fun getFavoriteWeatherList(): List<FavoriteWeather> {
        return db.getFavoriteDao().getAllFavoritesList() // פונקציה חדשה ב-DAO
    }


    suspend fun insertFavoriteWeather(favoriteWeather: FavoriteWeather) {
        db.getFavoriteDao().insertFavorite(favoriteWeather)
    }

    fun getFavoriteWeather() = db.getFavoriteDao().getAllFavorites()

    suspend fun deleteFavoriteWeather(favoriteWeather: FavoriteWeather) {
        db.getFavoriteDao().deleteFavorite(favoriteWeather)
    }

    suspend fun updateFavoriteWeather(favorites: List<FavoriteWeather>) {
        favorites.forEach { favorite ->
            db.getFavoriteDao().insertFavorite(favorite) // שומר את המידע המעודכן
        }
    }
    //--------------------------------------------

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
