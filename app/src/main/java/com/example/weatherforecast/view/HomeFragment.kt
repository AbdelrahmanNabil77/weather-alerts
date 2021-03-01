package com.example.weatherforecast.view

import android.os.Bundle
import android.text.Layout
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.weatherforecast.R
import com.example.weatherforecast.databinding.FragmentHomeBinding
import com.example.weatherforecast.model.Constants
import com.example.weatherforecast.utilities.Utility
import com.example.weatherforecast.viewmodel.HomeViewModel
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


class HomeFragment : Fragment(R.layout.fragment_home) {
    private var _binding: FragmentHomeBinding? = null
    // This property is only valid between onCreateView and
// onDestroyView.
    private val binding get() = _binding!!
    lateinit var calendar: Calendar
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewModel=ViewModelProvider(this).get(HomeViewModel::class.java)
        val simpleDateFormat = SimpleDateFormat("hh:mm");
        viewModel.fetchData(40.712776,-74.005974)
        viewModel.weatherLiveDate.observe(viewLifecycleOwner,{
            binding.tempTV.text=it.current?.temp.toString()+"F"
            binding.dateTV.text=DateFormat.getDateInstance().format(Utility.getCurrentDateAndTime().time)
            binding.timeTV.text=simpleDateFormat.format(Utility.getCurrentDateAndTime().time)
            binding.humidityTV.text=it.current?.humidity.toString()
            binding.windSpeedTV.text=it.current?.windSpeed.toString()
            binding.pressureTV.text=it.current?.pressure.toString()
            binding.cloudTV.text=it.current?.clouds.toString()
            binding.descriptionTV.text= it.current?.weather?.get(0)?.description
            binding.timezoneTV.text=it.timezone
            binding.weatherIcon.background=Utility.getIcon(this.context, it.current?.weather!![0]?.icon.toString())
            Log.d(Constants.logTag,"TIMEZONE IS: "+it.timezone)

        })
        binding.weekForecast.setOnClickListener {
        val action=HomeFragmentDirections.actionHomeFragmentToDaysFragment()
            findNavController().navigate(action)
        }

    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}