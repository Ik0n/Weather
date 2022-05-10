package com.example.weather.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.weather.model.RemoteDataSource
import com.example.weather.model.WeatherDTO
import com.example.weather.repository.DetailsRepository
import com.example.weather.repository.DetailsRepositoryImpl
import com.example.weather.utils.convertDtoToModel
import retrofit2.Callback

private const val SERVER_ERROR = "Ошибка сервера"
private const val REQUEST_ERROR = "Ошибка запроса на сервер"
private const val CORRUPTED_DATA = "Неполные данные"

class DetailsViewModel(
    val detailsLiveData : MutableLiveData<AppState> = MutableLiveData(),
    private val detailsRepository: DetailsRepository = DetailsRepositoryImpl(RemoteDataSource())
) : ViewModel() {

    private val callback  = object : Callback<WeatherDTO> {
        override fun onResponse(
            call: retrofit2.Call<WeatherDTO>,
            response: retrofit2.Response<WeatherDTO>
        ) {
            val serverResponse : WeatherDTO? = response.body()

            detailsLiveData.postValue(
                if (response.isSuccessful && serverResponse != null) {
                    checkResponse(serverResponse)
                } else {
                    AppState.Error(Throwable(SERVER_ERROR))
                }
            )
        }

        override fun onFailure(call: retrofit2.Call<WeatherDTO>, t: Throwable) {
            detailsLiveData.postValue(AppState.Error(Throwable(t.message ?: REQUEST_ERROR)))
        }

        private fun checkResponse(serverResponse: WeatherDTO): AppState {
            val fact = serverResponse.fact
            return if (
                fact == null ||
                fact.temp == null ||
                fact.feels_like == null ||
                fact.condition.isNullOrEmpty()) {
                AppState.Error(Throwable(CORRUPTED_DATA))
            } else {
                AppState.Success(convertDtoToModel(serverResponse))
            }
        }
    }

    fun getWeather(lat: Double, lon: Double) {
        detailsLiveData.value = AppState.Loading
        detailsRepository.getWeatherDetailsFromServer(lat, lon, callback)
    }

}