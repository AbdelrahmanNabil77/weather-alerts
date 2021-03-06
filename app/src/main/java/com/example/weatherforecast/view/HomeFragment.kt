package com.example.weatherforecast.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.weatherforecast.databinding.FragmentHomeBinding
import com.example.weatherforecast.model.Constants
import com.example.weatherforecast.model.Weather
import com.example.weatherforecast.utilities.Utility
import com.example.weatherforecast.viewmodel.HomeViewModel
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
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
        val viewModel=ViewModelProvider(this).get(HomeViewModel::class.java)
        val simpleDateFormat = SimpleDateFormat("hh:mm");
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        done=MutableLiveData()
        done.postValue(false)
        getLocation()
        done.observe(viewLifecycleOwner, {
            if (it) {
                if (isOnline(requireContext())) {
                    viewModel.fetchData(latLng.latitude, latLng.longitude)
                } else {
                    noInternetDialog()
                }
            }
        })
        viewModel.weatherLiveDate.observe(viewLifecycleOwner, {
            viewModel.insertHomeWeather(it)
        })
        viewModel.getCurrentWeather(this).observe(viewLifecycleOwner, {
            if (it != null) {
                weather = it
                binding.tempTV.text = it.current?.temp.toString()
                binding.dateTV.text = DateFormat.getDateInstance().format(Utility.getCurrentDateAndTime().time)
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
    fun isOnline(context: Context): Boolean {
        val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager != null) {
            val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    Log.d(Constants.logTag, "NetworkCapabilities.TRANSPORT_CELLULAR")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    Log.d(Constants.logTag, "NetworkCapabilities.TRANSPORT_WIFI")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                    Log.d(Constants.logTag, "NetworkCapabilities.TRANSPORT_ETHERNET")
                    return true
                }
            }
        }
        return false
    }
    private fun noInternetDialog() {
        AlertDialog.Builder(requireContext()).setTitle("Network Error").setMessage("Ooops!!\nThe application needs to be connected" +
                "to the internet at least for the first time.\n" +
                "May you switch on the internet connection and restart the application?").setPositiveButton("Restart") { dialog, which ->
            val intent = requireActivity().intent
            requireActivity().finish()
            startActivity(intent)
        }.setNegativeButton("Exit") { dialog, which -> requireActivity().finish() }.show()
    }
}