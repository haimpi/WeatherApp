/*
package com.example.weatherapp.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import com.example.weatherapp.R
import com.example.weatherapp.databinding.ActivitySplashBinding
import com.example.weatherapp.databinding.ActivityWeatherBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    private val viewModel: WeatherViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivitySplashBinding.inflate(layoutInflater)

        setContentView(binding.root)

        val animationView = findViewById<LottieAnimationView>(R.id.splash_animation)
        animationView.setAnimation(R.raw.splash)
        animationView.playAnimation()

        viewModel.isLoading.observe(this) { isLoading ->
            if (!isLoading) {
                startActivity(Intent(this, WeatherActivity::class.java))
                finish()
            }
        }
    }
}
*/