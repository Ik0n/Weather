package com.example.weather.view.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weather.R
import com.example.weather.databinding.FragmentDetailsBinding
import com.example.weather.model.Weather
import java.text.SimpleDateFormat
import java.util.*

class DetailsFragment : Fragment() {
    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val weather = arguments?.getParcelable<Weather>(BUNDLE_EXTRA)?.let { weather ->
            weather.city.also {
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
                temperatureValue.text = weather.temperature.toString()
                feelsLikeValue.text = weather.feelsLike.toString()

                // TODO with API data
                detailsTime.text = SimpleDateFormat("HH:mm").format(Date(System.currentTimeMillis()))
                wind.text = "3,1 м/c, СВ"
                humidity.text = "69%"
                atmosphericPressure.text = "750 мм рт. ст."

                detailsHoursRecycler.layoutManager = LinearLayoutManager(
                    view.context,
                    LinearLayoutManager.HORIZONTAL,
                    false
                )
                detailsHoursRecycler.adapter = HoursTempAdapter()
            }
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
}