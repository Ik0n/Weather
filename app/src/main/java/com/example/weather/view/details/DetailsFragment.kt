package com.example.weather.view.details

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weather.R
import com.example.weather.databinding.FragmentDetailsBinding
import com.example.weather.model.Weather
import com.example.weather.model.WeatherDTO
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*

const val DETAILS_INTENT_FILTER = "DETAILS INTENT FILTER"

const val DETAILS_LOAD_RESULT_EXTRA = "LOAD RESULT"
const val DETAILS_INTENT_EMPTY_EXTRA = "INTENT IS EMPTY"
const val DETAILS_DATA_EMPTY_EXTRA = "DATA IS EMPTY"
const val DETAILS_RESPONSE_EMPTY_EXTRA = "RESPONSE IS EMPTY"
const val DETAILS_REQUEST_ERROR_EXTRA = "REQUEST ERROR"
const val DETAILS_URL_MALFORMED_EXTRA = "URL MALFORMED"
const val DETAILS_RESPONSE_SUCCESS_EXTRA = "RESPONSE SUCCESS"
const val DETAILS_WEATHER_EXTRA = "WEATHER"

class DetailsFragment : Fragment() {
    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = _binding!!

    private lateinit var weatherBundle : Weather

    private val onLoadListener : WeatherLoader.WeatherLoaderListener =
        object : WeatherLoader.WeatherLoaderListener {
        override fun onLoaded(weatherDTO: WeatherDTO) {
            displayWeather(weatherDTO)
        }

        override fun onFailed(throwable: Throwable) {
            throw Throwable(getString(R.string.error))
        }

    }

    private val loadResultReceiver : BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.getStringExtra(DETAILS_LOAD_RESULT_EXTRA)) {
                DETAILS_URL_MALFORMED_EXTRA -> {
                    intent.getStringExtra(DETAILS_URL_MALFORMED_EXTRA)?.let {
                        requireView().showSnackbarWithOutAction(it, Snackbar.LENGTH_SHORT)
                    }
                }
                DETAILS_REQUEST_ERROR_EXTRA -> {
                    intent.getStringExtra(DETAILS_REQUEST_ERROR_EXTRA)?.let {
                        requireView().showSnackbarWithOutAction(it, Snackbar.LENGTH_SHORT)
                    }
                }
                DETAILS_RESPONSE_EMPTY_EXTRA -> {
                    intent.getStringExtra(DETAILS_RESPONSE_EMPTY_EXTRA)?.let {
                        requireView().showSnackbarWithOutAction(it, Snackbar.LENGTH_SHORT)
                    }
                }
                DETAILS_INTENT_EMPTY_EXTRA -> {
                    intent.getStringExtra(DETAILS_INTENT_EMPTY_EXTRA)?.let {
                        requireView().showSnackbarWithOutAction(it, Snackbar.LENGTH_SHORT)
                    }
                }
                DETAILS_DATA_EMPTY_EXTRA -> {
                    intent.getStringExtra(DETAILS_DATA_EMPTY_EXTRA)?.let {
                        requireView().showSnackbarWithOutAction(it, Snackbar.LENGTH_SHORT)
                    }
                }
                DETAILS_RESPONSE_SUCCESS_EXTRA -> {
                    intent.getParcelableExtra<WeatherDTO>(DETAILS_WEATHER_EXTRA)?.let {
                            displayWeather(it)
                        }
                }
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        context?.let {
            LocalBroadcastManager.getInstance(it)
                .registerReceiver(loadResultReceiver, IntentFilter(DETAILS_INTENT_FILTER))
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        context?.let {
            LocalBroadcastManager.getInstance(it).unregisterReceiver(loadResultReceiver)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        weatherBundle = arguments?.getParcelable<Weather>(BUNDLE_EXTRA) ?: Weather()
        getWeather()
    }

    private fun getWeather() {
        with(binding) {
            mainView.visibility = View.GONE
            loadingLayout.visibility = View.VISIBLE
        }

        context?.let {
            it.startService(Intent(it, DetailsService::class.java).apply {
                putExtra(LATITUDE_EXTRA, weatherBundle.city.lat)
                putExtra(LONGITUDE_EXTRA, weatherBundle.city.lon)
            })
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val BUNDLE_EXTRA = "weather"

        fun newInstance(bundle : Bundle) : DetailsFragment =
            DetailsFragment().apply { arguments = bundle }
    }

    private fun displayWeather(weatherDTO: WeatherDTO) {
        var context = context
        with(binding) {
            mainView.visibility = View.VISIBLE
            loadingLayout.visibility = View.GONE

            weatherBundle.city.also {
                with(binding) {
                    cityName.text = it.city
                    cityCoordinates.text = String.format(
                        getString(R.string.city_coordinates),
                        it.lat.toString(),
                        it.lon.toString()
                    )
                }
            }

            with(binding) {
                temperatureValue.text = weatherDTO.fact?.temp.toString()
                feelsLikeValue.text = weatherDTO.fact?.feels_like.toString()

                detailsCondition.text = weatherDTO.fact?.condition.toString()
                detailsTime.text = SimpleDateFormat("HH:mm").format(Date(System.currentTimeMillis()))

                val temp = "https://yastatic.net/weather/i/icons/funky/dark/" + weatherDTO.fact?.icon + ".svg"
                Picasso.get().load(temp).into(detailsIcon)

                wind.text = weatherDTO.fact?.wind_speed.toString() + " " + weatherDTO.fact?.wind_dir.toString()
                humidity.text = weatherDTO.fact?.humidity.toString()
                atmosphericPressure.text = weatherDTO.fact?.pressure_mm.toString()

                detailsHoursRecycler.layoutManager = LinearLayoutManager(
                    context,
                    LinearLayoutManager.HORIZONTAL,
                    false
                )
                detailsHoursRecycler.adapter = HoursTempAdapter().also {
                    it.setHoursTemp(weatherDTO?.forecasts?.get(0))
                }
            }
        }
    }

    private fun View.showSnackbarWithOutAction (
        text : String,
        length : Int
    ) {
        Snackbar.make(this, text, length).show()
    }
}