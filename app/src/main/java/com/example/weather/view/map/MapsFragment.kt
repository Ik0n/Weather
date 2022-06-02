package com.example.weather.view.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.weather.R
import com.example.weather.databinding.FragmentMapsBinding
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.mapview.MapView

class MapsFragment : Fragment() {

    private val MAPKIT_API_KEY = "2ef0dc55-2367-4101-88fb-34c079b401e6"
    private val TARGET_LOCATION: Point = Point(59.945933, 30.320045)

    private var _binding: FragmentMapsBinding? = null
    private val binding get() = _binding!!

    private lateinit var yandexMap: MapView

    companion object {
        fun newInstance() = MapsFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        MapKitFactory.setApiKey(MAPKIT_API_KEY)
        MapKitFactory.initialize(view.context)

        yandexMap = view.findViewById(R.id.yandex_map)

        yandexMap.map.move(
                CameraPosition(TARGET_LOCATION, 14.0f, 0.0f, 0.0f),
                Animation(Animation.Type.SMOOTH, 5f),
                null
            )
    }


}