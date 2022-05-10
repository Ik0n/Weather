package com.example.weather.viewmodel

import com.example.weather.model.WeatherDTO

sealed class ResponseState {
    data class Success(val weather: WeatherDTO) : ResponseState()
    data class Error(val error: Throwable) : ResponseState()
    object Loading : ResponseState()
}