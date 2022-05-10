package com.example.weather.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.weather.view.details.WeatherLoader

class DetailsViewModel(
    private val liveDataToObserve : MutableLiveData<AppState> = MutableLiveData(),
    private val listener : WeatherLoader.WeatherLoaderListener
) : ViewModel() {

    fun getLiveData() = liveDataToObserve

    fun getDataFromServer() {

    }
}