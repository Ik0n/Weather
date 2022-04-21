package com.example.weather.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Weather(
    val city : City = getDefaultCity(),
    val temperature: Int = 0,
    val feelsLike: Int = 0
) : Parcelable

fun getDefaultCity() = City("Москва", 55.755826, 37.61729990000035)

fun getHoursTemp() = mapOf(
    Pair("00:00", 0),
    Pair("01:00", 1),
    Pair("02:00", 2),
    Pair("03:00", 3),
    Pair("04:00", 4),
    Pair("05:00", 5),
    Pair("06:00", 6),
    Pair("07:00", 7),
    Pair("08:00", 8),
    Pair("09:00", 9),
    Pair("10:00", 10),
    Pair("11:00", 11),
    Pair("12:00", 12),
    Pair("13:00", 13),
    Pair("14:00", 14),
    Pair("15:00", 15),
    Pair("16:00", 16),
    Pair("17:00", 17),
    Pair("18:00", 18),
    Pair("19:00", 19),
    Pair("20:00", 20),
    Pair("21:00", 21),
    Pair("22:00", 22),
    Pair("23:00", 23),
)

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
