package com.example.weather.model

data class WeatherDTO(
    val fact : FactDTO?,
    val forecasts: List<ForecastsDTO>?
)

data class FactDTO(
    val temp : Int?,
    val feels_like : Int?,
    val condition : String?,
    val wind_speed : Float?,
    val wind_dir : String?,
    val humidity : Int?,
    val pressure_mm : Int?,
    val icon : String?
)

data class ForecastsDTO(
    val hours : List<HourDTO>?
)

data class HourDTO(
    val hour : String?,
    val temp: Int?,
    val icon: String?
)