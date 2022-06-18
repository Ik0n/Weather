package com.example.weather.model

import com.example.weather.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.Request

private const val REQUEST_API_KEY = "X-Yandex-API-Key"
private const val MAIN_LINK = "https://api.weather.yandex.ru/v2/forecast?"

class RemoteDataSourceOkHttp {
    fun getWeatherDetails(requestLink: String, callback: okhttp3.Callback) {
        val builder: Request.Builder = Request.Builder().apply {
            header(REQUEST_API_KEY, BuildConfig.WEATHER_API_KEY)
            url(requestLink)
        }

        OkHttpClient().newCall(builder.build()).enqueue(callback)
    }
}