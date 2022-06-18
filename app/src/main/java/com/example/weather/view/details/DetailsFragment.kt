package com.example.weather.view.details

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.weather.R
import com.example.weather.databinding.FragmentDetailsBinding
import com.example.weather.model.Weather
import com.example.weather.utils.showSnackBar
import com.example.weather.viewmodel.DetailsViewModel
import com.example.weather.viewmodel.ResponseState
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

    private val viewModel: DetailsViewModel by lazy {
        ViewModelProvider(this).get(DetailsViewModel::class.java)
    }

    private lateinit var weatherBundle : Weather

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

        viewModel.detailsLiveData.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            renderData(it)
        })

        requestWeather()
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

    private fun renderData(responseState : ResponseState) {
        when(responseState) {
            is ResponseState.Success -> {
                binding.mainView.visibility = View.VISIBLE
                binding.loadingLayout.visibility = View.GONE
                setWeather(responseState.weather)
            }
            is ResponseState.Loading -> {
                binding.mainView.visibility = View.GONE
                binding.loadingLayout.visibility = View.VISIBLE
            }
            is ResponseState.Error -> {
                    binding.loadingLayout.visibility = View.GONE

                    binding.mainView.showSnackBar(
                        getString(R.string.error),
                        getString(R.string.reload),
                        { requestWeather() }
                    )
            }
        }
    }

    private fun setWeather(weather: Weather) {
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

            temperatureValue.text = weather.temperature.toString()
            feelsLikeValue.text = weather.feelsLike.toString()

            detailsCondition.text = weather.condition
            detailsTime.text = SimpleDateFormat("HH:mm").format(Date(System.currentTimeMillis()))

            weather.icon.let { icon ->
                context?.let {
                    Glide
                        .with(context)
                        .load("https://yastatic.net/weather/i/icons/funky/dark/$icon.svg")
                        .into(detailsIcon)
                }
            }
            wind.text = weather.wind_speed.toString() + " " + weather.wind_dir
            humidity.text = weather.humidity.toString()
            atmosphericPressure.text = weather.pressure_mm.toString()

            detailsHoursRecycler.layoutManager = LinearLayoutManager(
                context,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            detailsHoursRecycler.adapter = HoursTempAdapter().also {
                it.setHoursTemp(weather.forecasts)
            }
        }
    }

    private fun requestWeather() {
        viewModel.getWeather(weatherBundle.city.lat, weatherBundle.city.lon)
    }

}