package com.example.weather.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Weather(
    val city : City = getDefaultCity(),
    val temperature: Int = 0,
    val feelsLike: Int = 0,
    val condition : String = "",
    val wind_speed : Float = 0f,
    val wind_dir : String = "",
    val humidity : Int = 0,
    val pressure_mm : Int = 0,
    val icon : String = "",
    val forecasts : Forecasts = Forecasts(listOf())
) : Parcelable

@Parcelize
data class Forecasts(
    val hours : List<Hour>?
) : Parcelable

@Parcelize
data class Hour(
    val hour : String?,
    val temp: Int?,
    val icon: String?
) : Parcelable

fun getDefaultCity() = City("Москва", 55.755826, 37.61729990000035)

fun getWorldCities() = listOf(
        Weather(City("Лондон", 51.5085300, -0.1257400), 1, 2),
        Weather(City("Токио", 35.6895000, 139.6917100), 3, 4),
        Weather(City("Париж", 48.8534100, 2.3488000), 5, 6),
        Weather(City("Берлин", 52.52000659999999, 13.404953999999975), 7, 8),
)

fun getRussianCities() = listOf(
        Weather(City("Москва", 55.755826, 37.617299900000035), 1, 2),
        Weather(City("Санкт-Петербург", 59.9342802, 30.335098600000038), 3, 3),
        Weather(City("Новосибирск", 55.00835259999999, 82.93573270000002), 5, 6),
        Weather(City("Екатеринбург", 56.83892609999999, 60.60570250000001), 7, 8),
)
