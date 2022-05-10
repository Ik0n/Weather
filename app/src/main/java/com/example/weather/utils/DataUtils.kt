package com.example.weather.utils

import com.example.weather.model.*

fun convertDtoToModel(weatherDTO: WeatherDTO): List<Weather> {
    val fact: FactDTO? = weatherDTO.fact
    var hours : MutableList<Hour> = mutableListOf()
    weatherDTO?.forecasts?.get(0)?.hours?.forEach {
        hours.add(Hour(it.hour, it.temp, it.icon))
    }
    return listOf(
        Weather(
            getDefaultCity(),
            fact?.temp!!,
            fact?.feels_like!!,
            fact?.condition!!,
            fact?.wind_speed!!,
            fact?.wind_dir!!,
            fact?.humidity!!,
            fact?.pressure_mm!!,
            fact?.icon!!,
            Forecasts(hours)
        )
    )
}