package com.example.weather.view


import android.content.Context
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import com.example.weather.R
import com.example.weather.databinding.MainFragmentBinding
import com.example.weather.model.Weather
import com.example.weather.view.details.DetailsFragment
import com.example.weather.viewmodel.AppState
import com.example.weather.viewmodel.MainViewModel
import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import com.example.weather.model.City
import com.example.weather.view.contacts.REQUEST_CODE
import kotlinx.android.synthetic.main.main_fragment.*
import java.io.IOException

private const val IS_WORLD_KEY = "LIST_OF_TOWNS_KEY"
private const val REFRESH_PERIOD = 60000L
private const val MINIMAL_DISTANCE = 100f
private const val REQUEST_CODE = 707


class MainFragment : Fragment() {

    private var _binding: MainFragmentBinding? = null
    private val binding get() = _binding!!

    private var isDataSetRus : Boolean = false

    companion object {
        fun newInstance() = MainFragment()
    }

    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this).get(MainViewModel::class.java)
    }

    private val adapter = MainFragmentAdapter(object : MainFragmentAdapter.OnItemViewClickListener {
        override fun onItemViewClick(weather: Weather) {
            openDetailsFragment(weather)
        }
    })

    private val onLocationListener = LocationListener { location ->
        context?.let {
            getAddressAsync(it, location)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = MainFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            mainFragmentRecyclerView.adapter = adapter
            mainFragmentFAB.setOnClickListener{
                changeWeatherDataSet()
            }
        }

        binding.mainFragmentFABLocation.setOnClickListener {
            checkPermission()
        }

        val observer = Observer<AppState> {
            renderData(it)
        }

        viewModel.apply {
            getLiveData().observe(viewLifecycleOwner, observer)
        }

        showListOfTowns()


    }

    private fun checkPermission() {
        activity?.let {
            when {
                ContextCompat.checkSelfPermission(it,
                    Manifest.permission.ACCESS_FINE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED -> {
                    getLocation()
                }
                shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)
                -> {
                    showRationaleDialog()
                }
                else -> {
                    requestPermission()
                }
            }
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        checkPermissionsResult(requestCode, grantResults)
    }

    private fun checkPermissionsResult(requestCode: Int, grantResults: IntArray)
    {
        when (requestCode) {
            REQUEST_CODE -> {
                var grantedPermissions = 0
                if ((grantResults.isNotEmpty())) {
                    for (i in grantResults) {
                        if (i == PackageManager.PERMISSION_GRANTED) {
                            grantedPermissions++
                        }
                    }
                    if (grantResults.size == grantedPermissions) {
                        getLocation()
                    } else {
                        showDialog(
                            getString(R.string.dialog_title_no_gps),
                            getString(R.string.dialog_message_no_gps)
                        )
                    }
                } else {
                    showDialog(
                        getString(R.string.dialog_title_no_gps),
                        getString(R.string.dialog_message_no_gps)
                    )
                }
                return
            }
        }
    }


    private fun showDialog(title: String, message: String) {
        activity?.let {
            AlertDialog.Builder(it)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton(getString(R.string.dialog_button_close)) {
                        dialog, _ -> dialog.dismiss() }
                .create()
                .show()
        }
    }

    private fun getLocation() {
        activity?.let { context ->
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    val provider = locationManager.getProvider(LocationManager.GPS_PROVIDER)
                    provider?.let {
                        locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            REFRESH_PERIOD,
                            MINIMAL_DISTANCE,
                            onLocationListener
                        )
                    }
                } else {
                    val location =
                        locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    if (location == null) {
                        showDialog(
                            getString(R.string.dialog_title_gps_turned_off),
                            getString(R.string.dialog_message_last_location_unknown)
                        )
                    } else {
                        getAddressAsync(context, location)
                        showDialog(
                            getString(R.string.dialog_title_gps_turned_off),
                            getString(R.string.dialog_message_last_known_location)
                        )
                    }
                }
            } else {
                showRationaleDialog()
            }
        }
    }


    private fun getAddressAsync(
        context: Context,
        location: Location
    ) {
        val geoCoder = Geocoder(context)
        Thread {
            try {
                val addresses = geoCoder.getFromLocation(
                    location.latitude,
                    location.longitude,
                    1
                )
                mainFragmentFAB.post {
                    showAddressDialog(addresses[0].getAddressLine(0), location)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }.start()
    }


    private fun showAddressDialog(address: String, location: Location) {
        activity?.let {
            AlertDialog.Builder(it)
                .setTitle(getString(R.string.dialog_address_title))
                .setMessage(address)
                .setPositiveButton(getString(R.string.dialog_address_get_weather)) { _, _ ->
                    openDetailsFragment(
                        Weather(
                            City(
                                address,
                                location.latitude,
                                location.longitude
                            )
                        )
                    )
                }
                .setNegativeButton(getString(R.string.dialog_button_close)) {
                        dialog, _ -> dialog.dismiss() }
                .create()
                .show()
        }
    }


    private fun openDetailsFragment(
        weather: Weather
    ) {
        activity?.supportFragmentManager?.apply {
            beginTransaction()
                .add(
                    R.id.container,
                    DetailsFragment.newInstance(Bundle().apply {
                        putParcelable(DetailsFragment.BUNDLE_EXTRA, weather)
                    })
                )
                .addToBackStack("")
                .commitAllowingStateLoss()
        }
    }

    private fun showRationaleDialog() {
        activity?.let {
            AlertDialog.Builder(it)
                .setTitle(getString(R.string.dialog_rationale_title))
                .setMessage(getString(R.string.dialog_rationale_message))
                .setPositiveButton(getString(R.string.dialog_rationale_give_access))
                { _, _ -> requestPermission() }
                .setNegativeButton(getString(R.string.dialog_rationale_decline))
                { dialog, _ -> dialog.dismiss() }
                .create()
                .show()
        }
    }

    private fun requestPermission() {
        requestPermissions(
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            REQUEST_CODE
        )
    }

    private fun changeWeatherDataSet() {
        if (isDataSetRus) {
            viewModel.getWeatherFromLocalSourceRus()
            binding.mainFragmentFAB.setImageResource(R.drawable.ic_russia)
        } else {
            viewModel.getWeatherFromLocalSourceWorld()
            binding.mainFragmentFAB.setImageResource(R.drawable.ic_earth)
        }

        isDataSetRus = !isDataSetRus

        saveListOfTowns(isDataSetRus)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun renderData(appState : AppState) {
        when(appState) {
            is AppState.Success -> {
                val weatherData = appState.weatherData
                binding.includedLoadingLayout.loadingLayout.visibility = View.GONE
                adapter.setWeather(appState.weatherData)
            }
            is AppState.Loading -> binding.includedLoadingLayout.loadingLayout.visibility = View.VISIBLE
            is AppState.Error -> {
                binding.apply {
                    includedLoadingLayout.loadingLayout.visibility = View.GONE
                    mainFragmentFAB.showSnackbar(
                        getString(R.string.error),
                        getString(R.string.reload),
                        { viewModel.getWeatherFromLocalSourceRus() }
                    )
                }
            }
        }
    }

    private fun showListOfTowns() {
        activity?.let {
            if (it.getPreferences(Context.MODE_PRIVATE).getBoolean(IS_WORLD_KEY, false)) {
                changeWeatherDataSet()
            } else {
                viewModel.getWeatherFromLocalSourceRus()
            }
        }
    }

    private fun saveListOfTowns(isDataSetWorld: Boolean) {
        activity?.let {
            with(it.getPreferences(Context.MODE_PRIVATE).edit()) {
                putBoolean(IS_WORLD_KEY, isDataSetWorld)
                apply()
            }
        }
    }

    private fun View.showSnackbar(
        text : String,
        actionText : String,
        action : (View) -> Unit,
        length : Int = Snackbar.LENGTH_INDEFINITE
    ) {
        Snackbar.make(this, text, length).setAction(actionText, action).show()
    }

    private fun View.showSnackbarWithOutAction (
        text : String,
        length : Int
    ) {
        Snackbar.make(this, text, length).show()
    }
}