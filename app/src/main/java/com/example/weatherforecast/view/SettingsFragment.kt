package com.example.weatherforecast.view

import android.content.Intent.getIntent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.app.ActivityCompat.recreate
import androidx.lifecycle.ViewModelProvider
import com.example.weatherforecast.R
import com.example.weatherforecast.databinding.FragmentSettingsBinding
import com.example.weatherforecast.model.Constants
import com.example.weatherforecast.viewmodel.HomeViewModel
import com.example.weatherforecast.viewmodel.SettingsViewModel
import org.antlr.v4.runtime.misc.MurmurHash.finish
import java.util.*


class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    // This property is only valid between onCreateView and
// onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewModel= ViewModelProvider(this).get(SettingsViewModel::class.java)
        //val units= arrayOf("Choose the unit","Standard","Imperial","Metric")
        val unitsArrayAdapter=ArrayAdapter(requireContext(),R.layout.spinner_item,resources.getStringArray(R.array.unitsArr))
        binding.unitsSpinner.adapter=unitsArrayAdapter
        val localeArrayAdapter=ArrayAdapter(requireContext(),R.layout.spinner_item,resources.getStringArray(R.array.LocaleArr))
        binding.localeSpinner.adapter=localeArrayAdapter
        binding.unitsSpinner.onItemSelectedListener=object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if(position>0){
                    when(parent?.getItemAtPosition(position).toString()){
                        "Imperial"->{
                            viewModel.repository.setUnit(requireContext(),Constants.unitsImperial)
                            restart()

                        }
                        "Standard"->{
                            viewModel.repository.setUnit(requireContext(),Constants.unitsStandard)
                            restart()
                        }
                        "Metric"->{
                            viewModel.repository.setUnit(requireContext(),Constants.unitsMetric)
                            restart()
                        }
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }
        binding.localeSpinner.onItemSelectedListener=object :AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if(position>0){
                    when(parent?.getItemAtPosition(position).toString()){
                        "Arabic"->{
                            viewModel.repository.setLocale(requireContext(),"ar")
                            restart()

                        }
                        "English"->{
                            viewModel.repository.setLocale(requireContext(),"en")
                            restart()
                        }

                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }
    }

    private fun restart(){
        val intent = requireActivity().intent
        requireActivity().finish()
        startActivity(intent)
    }
    override fun onPrepareOptionsMenu(menu: Menu) {
        var item=menu.findItem(R.id.settingsFragment)
        item.setVisible(false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    }



