package com.example.weatherforecast.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.example.weatherforecast.R
import com.example.weatherforecast.adapter.HourAdapter
import com.example.weatherforecast.databinding.FragmentHoursBinding


class HoursFragment : Fragment() {
    private val args:HoursFragmentArgs by navArgs()
    private var _binding: FragmentHoursBinding? = null
    // This property is only valid between onCreateView and
// onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHoursBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.hrsRecView.adapter=HourAdapter(args.weather)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}