package com.example.weatherforecast.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.text.Layout
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.weatherforecast.R
import com.example.weatherforecast.databinding.FragmentHomeBinding
import com.example.weatherforecast.model.Constants
import com.example.weatherforecast.model.Weather
import com.example.weatherforecast.utilities.Utility
import com.example.weatherforecast.viewmodel.HomeViewModel
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


class HomeFragment : Fragment(R.layout.fragment_home) {
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
        val viewModel=ViewModelProvider(this).get(HomeViewModel::class.java)
        val simpleDateFormat = SimpleDateFormat("hh:mm");
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        done=MutableLiveData()
        done.postValue(false)
        getLocation()
        done.observe(viewLifecycleOwner,{
            if(it) {
                viewModel.fetchData(latLng.latitude, latLng.longitude)
            }
        })
        viewModel.weatherLiveDate.observe(viewLifecycleOwner,{
            weather=it
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
            if(weather!=null){
        val action=HomeFragmentDirections.actionHomeFragmentToDaysFragment(weather!!)
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
        locationRequest.interval = 100
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
        ActivityCompat.requestPermissions(requireActivity(), permissions as Array<String>, permissionID)

    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        if (requestCode==permissionID){
            if (grantResults[0]== PackageManager.PERMISSION_GRANTED){
                getLocation()
            }
            else{
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