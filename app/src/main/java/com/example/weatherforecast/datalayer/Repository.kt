package com.example.weatherforecast.datalayer

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.weatherforecast.datalayer.local.WeatherDB
import com.example.weatherforecast.datalayer.local.WeatherDao
import com.example.weatherforecast.datalayer.remote.WeatherApi
import com.example.weatherforecast.datalayer.remote.WeatherService
import com.example.weatherforecast.model.Constants
import com.example.weatherforecast.model.Current
import com.example.weatherforecast.model.Weather
import com.example.weatherforecast.view.HomeFragment
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class Repository() {
    lateinit var  remoteWeather:WeatherApi
    lateinit var weatherDB:WeatherDB
    lateinit var weatherDao:WeatherDao
    lateinit var liveCurrentWeather:MutableLiveData<Weather>
    constructor(application: Application) : this() {
        remoteWeather=WeatherService.getWeatherService()
        weatherDB=WeatherDB.getDatabase(application)
        weatherDao=weatherDB.WordDao()
        liveCurrentWeather= MutableLiveData()

    }
    fun insertHomeWeather(weather: Weather){
        CoroutineScope(Dispatchers.IO).launch {
            weatherDao.insertWeather(weather)
            liveCurrentWeather.postValue(weather)
        }
    }

    fun insertFavoriteWeather(weather: Weather){
        CoroutineScope(Dispatchers.IO).launch {
            weatherDao.insertWeather(weather)
        }
    }

    fun getFavoriteWeatherList(context: Context):LiveData<List<Weather>>{
        val sharedPref= context.getSharedPreferences(Constants.sharedPrefLocation,Context.MODE_PRIVATE)
        val lat=sharedPref.getString(Constants.sharedPrefLat,null)
        val lon=sharedPref.getString(Constants.sharedPrefLon,null)
        return weatherDao.getFilteredWeatherList(lat!!,lon!!)
    }



    fun saveCurrentLocation(context: Context, currentLocation:LatLng){
        val sharedPref= context.getSharedPreferences(Constants.sharedPrefLocation,Context.MODE_PRIVATE)
        val editor=sharedPref.edit()
        if(isNewLocation(context,currentLocation)) {
            val lat=sharedPref.getString(Constants.sharedPrefLat,null)
            val lon=sharedPref.getString(Constants.sharedPrefLon,null)
            CoroutineScope(Dispatchers.IO).launch {
                weatherDao.deleteOldHome(lat!!,lon!!)
            }
            editor.apply {
                putString(Constants.sharedPrefLat,""+ currentLocation.latitude)
                putString(Constants.sharedPrefLon,""+currentLocation.longitude)
            }.apply()
        }
    }



    fun isNewLocation(context: Context,newLocation:LatLng):Boolean{
        val sharedPref= context.getSharedPreferences(Constants.sharedPrefLocation,Context.MODE_PRIVATE)
        val lat=sharedPref.getString(Constants.sharedPrefLat,null)
        val lon=sharedPref.getString(Constants.sharedPrefLon,null)
        return if ((""+newLocation.latitude).equals(lat)&&(""+newLocation.longitude).equals(lon)){
            false
        }else{
            true
        }
    }

    fun getHomeWeather(context: Context,fragment: Fragment):LiveData<Weather>{
        val sharedPref= context.getSharedPreferences(Constants.sharedPrefLocation,Context.MODE_PRIVATE)
        val lat=sharedPref.getString(Constants.sharedPrefLat,null)
        val lon=sharedPref.getString(Constants.sharedPrefLon,null)
        Log.d(Constants.logTag,"fun"+"lat: "+lat.toString()+" lon: "+lon.toString())
        if(lat!=null&&lon!=null){
        weatherDao.getHomeWeather(lat!!,lon!!).observe(fragment,{
            liveCurrentWeather.postValue(it)
            Log.d(Constants.logTag,"observe"+"lat: "+lat.toString()+" lon: "+lon.toString())
        })}
        return liveCurrentWeather
    }
}