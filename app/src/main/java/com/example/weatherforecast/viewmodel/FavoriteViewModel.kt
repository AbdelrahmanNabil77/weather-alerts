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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FavoriteViewModel(var app: Application) : AndroidViewModel(app) {
    var repository: Repository
    var favoriteWeatherList:MutableLiveData<List<Weather>>
    var favoriteWeatherItem:MutableLiveData<Weather>
    init {
        repository = Repository(app)
        favoriteWeatherList= MutableLiveData<List<Weather>>()
        favoriteWeatherItem= MutableLiveData<Weather>()
    }

    fun insertFavoriteLocation(lat: Double, long: Double,context: Context) {
        val unit=repository.getUnit(context)
        val locale= Utility.getLocale(context)
        CoroutineScope(Dispatchers.IO).launch {
            val response = repository.remoteWeather.getWeather(lat, long, "minutely", Constants.APIKEY,unit,locale)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    insertFavoriteWeather(response.body()!!)
                }
            }
        }
    }

    fun insertFavoriteWeather(weather: Weather){
        repository.insertFavoriteWeather(weather)
    }

    fun getUnit(context: Context):String{
        return  repository.getUnit(context)
    }

    fun deleteFavoriteItem(weather: Weather){
        repository.deleteItem(weather.lat,weather.lon)
    }

    fun getFavoriteWeatherList(fragment: Fragment){
        repository.getFavoriteWeatherList(getApplication()).observe(fragment,{
           if (it!=null){
               favoriteWeatherList.postValue(it)
           }
        })
    }
}