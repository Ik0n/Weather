package com.example.weather.utils

import com.example.weather.model.*
import com.example.weather.room.HistoryEntity

fun convertDtoToModel(weatherDTO: WeatherDTO): Weather {
    val fact: FactDTO? = weatherDTO.fact
    val hours : MutableList<Hour> = mutableListOf()
    weatherDTO.forecasts?.get(0)?.hours?.forEach {
        hours.add(Hour(it.hour, it.temp, it.icon))
    }

    return Weather(
            getDefaultCity(),
            fact?.temp ?: 0,
            fact?.feels_like ?: 0,
            fact?.condition ?: "",
            fact?.wind_speed ?: 0.0f,
            fact?.wind_dir ?: "",
            fact?.humidity ?: 0,
            fact?.pressure_mm ?: 0,
            fact?.icon ?: "",
            Forecasts(hours)
        )
}

fun convertHistoryEntityToWeather(entityList: List<HistoryEntity>) : List<Weather> {
    return entityList.map {
        Weather(
            city = City(it.city, 0.0, 0.0),
            temperature = it.temperature,
            condition = it.condition
        )
    }
}

fun convertWeatherToHistoryEntity(weather: Weather) : HistoryEntity {
    return HistoryEntity(
        0,
        weather.city.city,
        weather.temperature,
        weather.condition
    )
}
