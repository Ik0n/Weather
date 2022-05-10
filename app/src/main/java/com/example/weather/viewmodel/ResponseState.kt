package com.example.weather.viewmodel

import com.example.weather.model.Weather

sealed class ResponseState {
    data class Success(val weather: Weather) : ResponseState()
    data class Error(val error: Throwable) : ResponseState()
    object Loading : ResponseState()
}