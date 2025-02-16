package com.example.weatherapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.databinding.ItemFavoriteWeatherBinding
import com.example.weatherapp.models.FavoriteWeather
import com.example.weatherapp.ui.CitySearchViewModel
import java.util.Locale

/**
 *  מתאם להצגת רשימת הערים המועדפות ברשימת RecyclerView.
 * כולל הצגת נתוני מזג האוויר ועדכון אייקון מזג האוויר.
 */
class FavoriteWeatherAdapter(
    private val viewModel: CitySearchViewModel,  // גישה למודל הנתונים כדי לקבל אייקונים של מזג אוויר
    private val onDeleteClick: (FavoriteWeather) -> Unit // פונקציה להפעלת מחיקה בלחיצה
) : RecyclerView.Adapter<FavoriteWeatherAdapter.FavoriteViewHolder>() {

    //  רשימה של כל המועדפים (מתעדכן אוטומטית)
    private var favorites: List<FavoriteWeather> = emptyList()

    /**
     *  מחזיק את התצוגה של כרטיס עיר מועדפת
     */
    inner class FavoriteViewHolder(private val binding: ItemFavoriteWeatherBinding) :
        RecyclerView.ViewHolder(binding.root) {

        /**
         *  קושר נתוני עיר מועדפת לרכיבים בתצוגה
         */
        fun bind(favorite: FavoriteWeather) {
            // המרת קוד מדינה לשם מלא (ישראל, ארה"ב וכו')
            val countryName = Locale("", favorite.country).displayCountry

            // הצגת פרטי העיר
            binding.tvCityAndCountry.text = "${favorite.cityName}, $countryName"
            binding.tvTemperature.text = "${favorite.temperature}°C"
            binding.tvDescription.text = favorite.description

            // שינוי האייקון של מזג האוויר לפי הנתונים שנשמרו
            val iconRes = viewModel.getWeatherIcon(favorite.iconCode)
            binding.ivWeatherIcon.setImageResource(iconRes)

            // בעת לחיצה על כפתור המחיקה
            binding.ivDeleteFavorite.setOnClickListener {
                onDeleteClick(favorite)
            }
        }
    }

    /**
     *  יצירת ViewHolder חדש עבור כל כרטיסייה שמוצגת ברשימה
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val binding = ItemFavoriteWeatherBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FavoriteViewHolder(binding)
    }

    /**
     *  מחבר את הנתונים של כרטיס ספציפי ל-ViewHolder שלו
     */
    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        holder.bind(favorites[position])
    }

    /**
     *  מחזיר את מספר הפריטים ברשימה
     */
    override fun getItemCount() = favorites.size

    /**
     *  מקבל רשימה מעודכנת ומרענן את התצוגה
     */
    fun submitList(newList: List<FavoriteWeather>) {
        favorites = newList
        notifyDataSetChanged()
    }
}
