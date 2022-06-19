package com.example.weather.history

import com.example.weather.model.Weather

interface LocalRepository {
    fun getAllHistory(): List<Weather>
    fun saveEntity(weather: Weather)
    fun deleteAll()
}