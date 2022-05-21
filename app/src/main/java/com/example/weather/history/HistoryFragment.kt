package com.example.weather.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.weather.R
import com.example.weather.databinding.HistoryLayoutBinding
import com.example.weather.utils.showSnackBar
import com.example.weather.viewmodel.AppState
import kotlinx.android.synthetic.main.history_layout.*

class HistoryFragment : Fragment() {
    private var _binding: HistoryLayoutBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HistoryViewModel by lazy {
        ViewModelProvider(this).get(HistoryViewModel::class.java)
    }
    private val adapter: HistoryAdapter by lazy { HistoryAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = HistoryLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        historyLayoutRecyclerView.adapter = adapter
        viewModel.historyLiveData.observe(viewLifecycleOwner, Observer {
            renderData(it)
        })
        viewModel.getAllHistory()
    }

    private fun renderData(appState: AppState) {
        when(appState) {
            is AppState.Success -> {
                binding.historyLayoutRecyclerView.visibility = View.VISIBLE
                binding.includedLoadingLayout.loadingLayout.visibility = View.GONE
                adapter.setData(appState.weatherData)
            }
            is AppState.Loading -> {
                binding.historyLayoutRecyclerView.visibility = View.GONE
                binding.includedLoadingLayout.loadingLayout.visibility = View.VISIBLE
            }
            is AppState.Error -> {
                binding.historyLayoutRecyclerView.visibility = View.VISIBLE
                binding.includedLoadingLayout.loadingLayout.visibility = View.GONE
                binding.historyLayoutRecyclerView.showSnackBar(
                    getString(R.string.error),
                    getString(R.string.reload),
                    {
                        viewModel.getAllHistory()
                    }
                )
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = HistoryFragment()
    }
}