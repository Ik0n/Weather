package com.example.weather.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.weather.model.Repository
import com.example.weather.model.RepositoryImpl
import java.lang.Exception
import java.lang.Thread.sleep
import kotlin.random.Random

class MainViewModel(
    private val liveDataToObserve : MutableLiveData<AppState> = MutableLiveData(),
    private val repositoryImpl: Repository = RepositoryImpl()
) : ViewModel() {

    fun getLiveData() : LiveData<AppState> {
        return liveDataToObserve
    }

    fun getWeather() = getDataFromLocalSource()

    private fun getDataFromLocalSource() {
        liveDataToObserve.value = AppState.Loading
        Thread {
            sleep(2000)
            val flag = Random.nextInt(2) == 0
            if (flag) {
                liveDataToObserve.postValue(
                    AppState.Success(repositoryImpl.getWeatherFromLocalStorage())
                )
            } else {
                liveDataToObserve.postValue(AppState.Error(Throwable("Error")))
            }
        }.start()
    }
}