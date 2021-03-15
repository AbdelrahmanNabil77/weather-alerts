package com.example.weatherforecast.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.R
import androidx.navigation.fragment.findNavController
import com.example.weatherforecast.NavGraphDirections
import com.example.weatherforecast.adapter.HourAdapter
import com.example.weatherforecast.databinding.FragmentHomeBinding
import com.example.weatherforecast.model.Constants
import com.example.weatherforecast.model.Weather
import com.example.weatherforecast.utilities.Utility
import com.example.weatherforecast.utilities.Utility.Companion.isOnline
import com.example.weatherforecast.viewmodel.HomeViewModel
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.text.DateFormat
import java.text.SimpleDateFormat


class HomeFragment : Fragment() {
    lateinit var fusedLocationProviderClient : FusedLocationProviderClient
    val permissionID=7
    lateinit var latLng: LatLng
    lateinit var done:MutableLiveData<Boolean>
    private var _binding: FragmentHomeBinding? = null
    // This property is only valid between onCreateView and
// onDestroyView.
    private val binding get() = _binding!!
    var weather: Weather?=null

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
        if(Utility.isMorning()){
            binding.weekForecast.backgroundTintList=context?.resources?.getColorStateList(com.example.weatherforecast.R.color.sun)
            binding.hoursForecast.backgroundTintList=context?.resources?.getColorStateList(com.example.weatherforecast.R.color.sun)
        }else{
            binding.weekForecast.backgroundTintList=context?.resources?.getColorStateList(com.example.weatherforecast.R.color.night)
            binding.hoursForecast.backgroundTintList=context?.resources?.getColorStateList(com.example.weatherforecast.R.color.night)
        }

        val viewModel=ViewModelProvider(this).get(HomeViewModel::class.java)
        binding.tempUnit.text=Utility.getTempUnit(requireContext())
        binding.windSpeedUnit.text=Utility.getWindSpeedUnit(requireContext())
        val simpleDateFormat = SimpleDateFormat("hh:mm");
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        done=MutableLiveData()
        done.postValue(false)
        getLocation()
        binding.hoursForecast.setOnClickListener {
            if(weather!=null){
            val action = NavGraphDirections.actionGlobalHoursFragment(weather!!)
            findNavController().navigate(action)
        }

        }
        done.observe(viewLifecycleOwner, {
            if (it) {
                if (isOnline(requireContext())) {
                    viewModel.fetchData(latLng.latitude, latLng.longitude,requireContext())
                } else {
                    //noInternetDialog()
                }
            }
        })
        viewModel.weatherLiveDate.observe(viewLifecycleOwner, {
            viewModel.insertHomeWeather(it)
        })
        viewModel.getCurrentWeather(this).observe(viewLifecycleOwner, {
            if (it != null) {
                weather = it
                val simpleDateFormat = SimpleDateFormat("hh:mm")
                binding.tempTV.text = it.current?.temp.toString()
                binding.dateTV.text = Utility.getDate(it.current!!.dt!!.toInt(),it.timezoneOffset!!)//DateFormat.getDateInstance().format(Utility.getCurrentDateAndTime().time)
                binding.timeTV.text = simpleDateFormat.format(Utility.getCurrentDateAndTime().time)
                binding.humidityTV.text = it.current?.humidity.toString()
                binding.windSpeedTV.text = it.current?.windSpeed.toString()
                binding.pressureTV.text = it.current?.pressure.toString()
                binding.cloudTV.text = it.current?.clouds.toString()
                binding.descriptionTV.text = it.current?.weather?.get(0)?.description
                binding.timezoneTV.text = it.timezone
                binding.weatherIcon.background = Utility.getIcon(this.context, it.current?.weather!![0]?.icon.toString())
                Log.d(Constants.logTag, "TIMEZONE IS: " + it.timezone)
            }
        })
        binding.weekForecast.setOnClickListener {
            if(weather!=null){
                //val action=HomeFragmentDirections.actionHomeFragmentToDaysFragment(weather!!)
                val action=HomeFragmentDirections.actionGlobalDaysFragment(weather!!)
                findNavController().navigate(action)
            }
        }

    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @SuppressLint("MissingPermission")
    private fun getLocation(){
        if(isPermissionsGranted()){
            if (isLocationEnabled()){
                fusedLocationProviderClient.lastLocation.addOnCompleteListener {
                    requestLocation()
                }
            } else{
                enableLocation()
            }
        }else{
            requestPermission()
        }
    }


    @SuppressLint("MissingPermission")
    private fun requestLocation(){
        val locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 60000
        //locationRequest.numUpdates=1
        fusedLocationProviderClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.myLooper()
        )
    }

    private var locationCallback=object: LocationCallback(){
        override fun onLocationResult(p0: LocationResult?) {
            super.onLocationResult(p0)
            var locationRequest=p0?.lastLocation
            latLng= LatLng(locationRequest!!.latitude, locationRequest!!.longitude)
            done.postValue(true)

        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager = activity?.getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private fun enableLocation(){
        Toast.makeText(activity, "could you enable your location?", Toast.LENGTH_SHORT).show()
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        startActivity(intent)
    }

    private fun isPermissionsGranted(): Boolean {
        return if (ActivityCompat.checkSelfPermission(
                        requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            true
        } else {
            false
        }
    }

    //request permission for accessing location
    private fun requestPermission(){
        var permissions= arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
        )
        requestPermissions(permissions, permissionID)

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.d(Constants.logTag, "req " + requestCode)
        if (requestCode==permissionID){
            if (grantResults.get(0)==PackageManager.PERMISSION_GRANTED){
                Log.d(Constants.logTag, "GRANTED")
                getLocation()
            }else{
                Log.d(Constants.logTag, "DENIED")
                warningDialog()
            }
        }
    }

    private fun warningDialog(){
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle("Warning")
        builder.setMessage(
                "without this permission the app will not be able to work properly!\n " +
                        "Would you like to give us the permission?"
        )

        builder.setPositiveButton("Yes") { dialog, which ->
            requestPermission()
        }
        builder.setNegativeButton("No") { dialog, which ->
        }
        builder.show()
    }





}