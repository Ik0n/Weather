package com.example.weather.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class WeatherDTO(
    val fact : FactDTO?,
    val forecasts: List<ForecastsDTO>?
) : Parcelable

@Parcelize
data class FactDTO(
    val temp : Int?,
    val feels_like : Int?,
    val condition : String?,
    val wind_speed : Float?,
    val wind_dir : String?,
    val humidity : Int?,
    val pressure_mm : Int?,
    val icon : String?
) : Parcelable

@Parcelize
data class ForecastsDTO(
    val hours : List<HourDTO>?
) : Parcelable

@Parcelize
data class HourDTO(
    val hour : String?,
    val temp: Int?,
    val icon: String?
) : Parcelable