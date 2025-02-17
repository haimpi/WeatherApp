package com.example.weatherapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R
import com.example.weatherapp.databinding.ItemFavoriteWeatherBinding
import com.example.weatherapp.models.FavoriteWeather
import com.example.weatherapp.ui.CitySearchViewModel
import com.example.weatherapp.util.WeatherIconProvider
import com.example.weatherapp.util.convertUnixToTime
import java.util.Locale

/**
 *  מתאם להצגת רשימת הערים המועדפות ברשימת RecyclerView.
 *  כולל הצגת נתוני מזג האוויר ועדכון אייקון מזג האוויר.
 */
class FavoriteWeatherAdapter(
    private val viewModel: CitySearchViewModel,
    private val onDeleteClick: (FavoriteWeather) -> Unit
) : RecyclerView.Adapter<FavoriteWeatherAdapter.FavoriteViewHolder>() {

    private var favorites: List<FavoriteWeather> = emptyList()

    inner class FavoriteViewHolder(private val binding: ItemFavoriteWeatherBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(favorite: FavoriteWeather) {
            val countryName = Locale("", favorite.country).displayCountry

            with(binding) {
                tvCityAndCountry.text = "${favorite.cityName}, $countryName"
                tvTemperature.text = itemView.context.getString(R.string.label_temp, favorite.temperature ?: "--")
                tvDescription.text = itemView.context.getString(R.string.label_weather_description, favorite.description ?: "--")
                tvMinTemperature.text = itemView.context.getString(R.string.label_temp_min, favorite.minTemp ?: "--")
                tvMaxTemperature.text = itemView.context.getString(R.string.label_temp_max, favorite.maxTemp ?: "--")
                tvFeelsLike.text = itemView.context.getString(R.string.label_feels_like, favorite.feelsLike ?: "--")
                tvHumidity.text = itemView.context.getString(R.string.label_humidity, favorite.humidity ?: "--")
                tvWindSpeed.text = itemView.context.getString(R.string.label_wind, favorite.windSpeed ?: "--")
                tvSunrise.text = itemView.context.getString(R.string.label_sunrise, convertUnixToTime(favorite.sunrise, favorite.timezone))
                tvSunset.text = itemView.context.getString(R.string.label_sunset, convertUnixToTime(favorite.sunset, favorite.timezone))

                // שינוי האייקון של מזג האוויר
                val iconRes = WeatherIconProvider.getWeatherIcon(favorite.iconCode)
                ivWeatherIcon.setImageResource(iconRes)


                // לחיצה על כפתור מחיקה
                ivDeleteFavorite.setOnClickListener {
                    onDeleteClick(favorite)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val binding = ItemFavoriteWeatherBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FavoriteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        holder.bind(favorites[position])
    }

    override fun getItemCount() = favorites.size

    fun submitList(newList: List<FavoriteWeather>) {
        favorites = newList
        notifyDataSetChanged()
    }
}

