package com.example.weather.view.details

import android.app.IntentService
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.weather.BuildConfig
import com.example.weather.model.WeatherDTO
import com.google.gson.Gson
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.Exception
import java.net.MalformedURLException
import java.net.URL
import java.util.stream.Collectors
import javax.net.ssl.HttpsURLConnection

const val LATITUDE_EXTRA = "Latitude"
const val LONGITUDE_EXTRA = "Longitude"

private const val REQUEST_GET = "GET"
private const val REQUEST_TIMEOUT = 10000
private const val REQUEST_API_KEY = "X-Yandex-API-Key"

class DetailsService(name: String = "DetailService") : IntentService(name) {

    private val broadcastIntent = Intent(DETAILS_INTENT_FILTER)

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onHandleIntent(intent: Intent?) {
        intent?.let {
            val lat = intent.getDoubleExtra(LATITUDE_EXTRA, 0.0)
            val lon = intent.getDoubleExtra(LONGITUDE_EXTRA, 0.0)

            if (lat == 0.0 && lon == 0.0) {
                onEmptyData()
            } else {
                loadWeather(lat.toString(), lon.toString())
            }
        } ?: run {
            onEmptyIntent()
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun loadWeather(lat: String, lon: String) {
        try {
            val uri =
                URL("https://api.weather.yandex.ru/v2/forecast?" +
                        "lat=${lat}&" +
                        "lon=${lon}&" +
                        "extra=true"
                )

            lateinit var urlConnection: HttpsURLConnection

            try {
                urlConnection = (uri.openConnection() as HttpsURLConnection).apply {
                    requestMethod = REQUEST_GET
                    readTimeout = REQUEST_TIMEOUT

                    addRequestProperty(REQUEST_API_KEY, BuildConfig.WEATHER_API_KEY)
                }

                val weatherDTO: WeatherDTO =
                    Gson().fromJson(
                        getLines(BufferedReader(InputStreamReader(urlConnection.inputStream))),
                        WeatherDTO::class.java
                    )

                onResponse(weatherDTO)

            } catch (e: Exception) {
                onErrorRequest(e.message ?: "Empty error")
            } finally {
                urlConnection.disconnect()
            }
        } catch (e: MalformedURLException) {
            onMalformedURL()
        }
    }

    private fun onResponse(weatherDTO: WeatherDTO) {
        weatherDTO.fact?.let {
            onSuccessfulResponse(weatherDTO)
        } ?: run {
            onEmptyResponse()
        }
    }

    private fun onSuccessfulResponse(weatherDTO: WeatherDTO?) {
        val broadcastInt = Intent(DETAILS_INTENT_FILTER)
        broadcastInt.putExtra(DETAILS_LOAD_RESULT_EXTRA, DETAILS_RESPONSE_SUCCESS_EXTRA)

        broadcastInt.putExtra(DETAILS_WEATHER_EXTRA, weatherDTO)

        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastInt)
    }

    private fun onMalformedURL() {
        val broadcastInt = Intent(DETAILS_INTENT_FILTER).apply {
            putExtra(DETAILS_LOAD_RESULT_EXTRA, DETAILS_URL_MALFORMED_EXTRA)
        }
        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastInt)
    }

    private fun onErrorRequest(error: String) {
        val broadcastInt = Intent(DETAILS_INTENT_FILTER).apply {
            putExtra(DETAILS_LOAD_RESULT_EXTRA, DETAILS_REQUEST_ERROR_EXTRA)
            putExtra(DETAILS_REQUEST_ERROR_EXTRA, error)
        }
        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastInt)
    }

    private fun onEmptyResponse() {
        val broadcastInt = Intent(DETAILS_INTENT_FILTER).apply {
            putExtra(DETAILS_LOAD_RESULT_EXTRA, DETAILS_RESPONSE_EMPTY_EXTRA)
        }
        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastInt)
    }

    private fun onEmptyIntent() {
        val broadcastInt = Intent(DETAILS_INTENT_FILTER).apply {
            putExtra(DETAILS_LOAD_RESULT_EXTRA, DETAILS_INTENT_EMPTY_EXTRA)
        }
        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastInt)
    }

    private fun onEmptyData() {
        val broadcastInt = Intent(DETAILS_INTENT_FILTER).apply {
            putExtra(DETAILS_LOAD_RESULT_EXTRA, DETAILS_DATA_EMPTY_EXTRA)
        }
        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastInt)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun getLines(reader: BufferedReader) =
        reader.lines().collect(Collectors.joining("\n"))

}