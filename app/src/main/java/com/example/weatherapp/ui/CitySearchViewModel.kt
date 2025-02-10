package com.example.weatherapp.ui
//----------arthur code-------------
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.models.WeatherResponse
import com.example.weatherapp.repository.WeatherRepository
import com.example.weatherapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CitySearchViewModel @Inject constructor(
    private val repository: WeatherRepository
) : ViewModel() {

    val weatherData: MutableLiveData<Resource<WeatherResponse>> = MutableLiveData()

    fun getWeatherByCity(city: String, country: String) {
        weatherData.value = Resource.Loading() // ⏳ מצב טעינה

        viewModelScope.launch {
            val response = repository.getCoordinates(city, country)

            if (response.isSuccessful) { // ✅ בדיקת הצלחה
                val cityList = response.body()
                val firstCity = cityList?.firstOrNull()

                firstCity?.let {
                    val weatherResponse = repository.getWeatherData(it.lat, it.lon, "metric")
                    handleWeatherResponse(weatherResponse)
                } ?: weatherData.postValue(Resource.Error("City not found"))
            } else {
                weatherData.postValue(Resource.Error("Failed to fetch city coordinates"))
            }
        }
    }

    private fun handleWeatherResponse(response: retrofit2.Response<WeatherResponse>) {
        if (response.isSuccessful) {
            response.body()?.let {
                weatherData.postValue(Resource.Success(it)) // ✅ הצלחה
            } ?: weatherData.postValue(Resource.Error("No data found"))
        } else {
            weatherData.postValue(Resource.Error("Failed to fetch weather"))
        }
    }
}

