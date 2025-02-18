package com.example.weatherapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.weatherapp.R
import com.example.weatherapp.Animations.Click_button_animation
import com.example.weatherapp.databinding.FragmentMainDashboardBinding
import il.co.syntax.fullarchitectureretrofithiltkotlin.utils.autoCleared

class MainDashboardFragment : Fragment() {

    private var binding: FragmentMainDashboardBinding by autoCleared()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.cardCitySearch.setOnClickListener {
            Click_button_animation.scaleView(it) {
                findNavController().navigate(R.id.action_mainDashboardFragment_to_citySearchFragment)
            }
        }

        binding.cardFavorites.setOnClickListener {
            Click_button_animation.scaleView(it) {
                findNavController().navigate(R.id.action_mainDashboardFragment_to_favoritesFragment)
            }
        }

        binding.cardLocalWeather.setOnClickListener {
            Click_button_animation.scaleView(it) {
                findNavController().navigate(R.id.action_mainDashboardFragment_to_weatherLocalFragment)
            }
        }

        binding.cardSettings.setOnClickListener {
            Click_button_animation.scaleView(it) {
                findNavController().navigate(R.id.action_mainDashboardFragment_to_weatherSettingsFragment)
            }
        }
    }
}



