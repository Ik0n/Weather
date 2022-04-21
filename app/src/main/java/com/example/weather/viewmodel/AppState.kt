package com.example.weather.viewmodel

import com.example.weather.model.Weather

sealed class AppState {
    data class Success(val weatherData : Weather) : AppState()
    data class Error(val error: Throwable) : AppState()
    object Loading : AppState()
}