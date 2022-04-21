package com.example.weather.view


import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import com.example.weather.R
import com.example.weather.databinding.MainFragmentBinding
import com.example.weather.model.Weather
import com.example.weather.view.details.DetailsFragment
import com.example.weather.viewmodel.AppState
import com.example.weather.viewmodel.MainViewModel

class MainFragment : Fragment() {

    private var _binding: MainFragmentBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance() = MainFragment()
    }

    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this).get(MainViewModel::class.java)
    }

    private var isDataSetRus : Boolean = true

    private val adapter = MainFragmentAdapter(object : MainFragmentAdapter.OnItemViewClickListener {
        override fun onItemViewClick(weather: Weather) {
            val fragmentManager = activity?.supportFragmentManager
            fragmentManager?.let {
                val bundle = Bundle().also {
                    it.putParcelable(DetailsFragment.BUNDLE_EXTRA, weather)
                }
                fragmentManager.beginTransaction()
                    .add(R.id.container, DetailsFragment.newInstance(bundle))
                    .addToBackStack("")
                    .commitAllowingStateLoss()
            }
            }
    })

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

        val observer = Observer<AppState> {
            renderData(it)
        }

        viewModel.apply {
            getLiveData().observe(viewLifecycleOwner, observer)
            getWeatherFromLocalSourceRus()
        }
    }

    private fun changeWeatherDataSet()  =
        if (isDataSetRus) {
            viewModel.getWeatherFromLocalSourceWorld()
            binding.mainFragmentFAB.setImageResource(R.drawable.ic_earth)
        } else {
            viewModel.getWeatherFromLocalSourceRus()
            binding.mainFragmentFAB.setImageResource(R.drawable.ic_russia)
        }.also { isDataSetRus = !isDataSetRus }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun renderData(appState : AppState) {
        when(appState) {
            is AppState.Success -> {
                val weatherData = appState.weatherData
                binding.mainFragmentLoadingLayout.visibility = View.GONE
                adapter.setWeather(appState.weatherData)
            }
            is AppState.Loading -> binding.mainFragmentLoadingLayout.visibility = View.VISIBLE
            is AppState.Error -> {
                binding.apply {
                    mainFragmentLoadingLayout.visibility = View.GONE
                    mainFragmentFAB.showSnackbar(
                        getString(R.string.error),
                        getString(R.string.reload),
                        { viewModel.getWeatherFromLocalSourceRus() }
                    )
                }
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