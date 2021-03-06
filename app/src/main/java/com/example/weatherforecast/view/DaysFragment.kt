package com.example.weatherforecast.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.example.weatherforecast.R
import com.example.weatherforecast.adapter.WeekAdapter
import com.example.weatherforecast.databinding.FragmentDaysBinding
import com.example.weatherforecast.databinding.FragmentSettingsBinding
import com.example.weatherforecast.model.DailyItem


class DaysFragment : Fragment() {
    private val args:DaysFragmentArgs by navArgs()
    lateinit var weekAdapter: WeekAdapter
    private var _binding: FragmentDaysBinding? = null
    // This property is only valid between onCreateView and
// onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDaysBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        weekAdapter= WeekAdapter(args.weather)
        binding.weekRecView.adapter=weekAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}