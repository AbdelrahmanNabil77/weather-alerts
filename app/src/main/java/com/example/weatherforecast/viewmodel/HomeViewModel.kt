package com.example.weatherforecast.viewmodel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.weatherforecast.datalayer.Repository
import com.example.weatherforecast.model.Constants
import com.example.weatherforecast.model.Weather
import com.example.weatherforecast.utilities.Utility
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.*

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    var repository: Repository
    var weatherLiveDate = MutableLiveData<Weather>()



    init {
        repository = Repository(application)
    }

    /*fun insertWeather(weather: Weather) {
        repository.insertWeather(weather)
    }*/

    fun fetchData(lat: Double, long: Double,context:Context) {
        val unit=repository.getUnit(context)
        val locale=Utility.getLocale(context)
        CoroutineScope(Dispatchers.IO).launch {
            val response = repository.remoteWeather.getWeather(lat, long, "minutely", Constants.APIKEY,unit,locale)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    weatherLiveDate.postValue(response.body())
                }
            }
        }
    }


    fun setUnit(context: Context,unit:String){
        repository.setUnit(context,unit)
    }

    fun insertHomeWeather(weather: Weather){
        repository.insertHomeWeather(weather)
        repository.saveCurrentLocation(getApplication(),LatLng(weather.lat,weather.lon))
    }

    fun getCurrentWeather(fragment: Fragment):LiveData<Weather>{
        return repository.getHomeWeather(getApplication(),fragment)
    }

    fun getUnit(context: Context):String{
        return  repository.getUnit(context)
    }

}