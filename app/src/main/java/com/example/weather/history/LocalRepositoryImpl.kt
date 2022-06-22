package com.example.weather.history

import com.example.weather.model.Weather
import com.example.weather.room.HistoryDao
import com.example.weather.utils.convertHistoryEntityToWeather
import com.example.weather.utils.convertWeatherToHistoryEntity

class LocalRepositoryImpl(private val localDataSource: HistoryDao) : LocalRepository {
    override fun getAllHistory(): List<Weather> {
        return convertHistoryEntityToWeather(localDataSource.all())
    }

    override fun saveEntity(weather: Weather) {
        localDataSource.insert(convertWeatherToHistoryEntity(weather))
    }

    override fun deleteAll() {
        localDataSource.deleteAll()
    }
}