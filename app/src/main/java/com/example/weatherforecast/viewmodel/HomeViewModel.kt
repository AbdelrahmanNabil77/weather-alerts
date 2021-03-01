package com.example.weatherforecast.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.weatherforecast.datalayer.Repository
import com.example.weatherforecast.model.Constants
import com.example.weatherforecast.model.Weather
import kotlinx.coroutines.*

class HomeViewModel(application: Application) : AndroidViewModel(application) {
lateinit var repository:Repository
var weatherLiveDate= MutableLiveData<Weather>()
init {
    repository= Repository()
}
    fun fetchData(lat:Double,long:Double){
        Log.d(Constants.logTag,"fun fetch")
        CoroutineScope(Dispatchers.IO).launch {
          val response=repository.remoteWeather.getWeather(lat,long,"hourly,minutely",Constants.APIKEY)
            withContext(Dispatchers.Main){
                if (response.isSuccessful){
                    weatherLiveDate.postValue(response.body())
                    Log.d(Constants.logTag,"FETCH "+response.body()!!.timezone.toString())
                }else{
                    Log.d(Constants.logTag,"FETCH FAILED "+response.errorBody().toString())
                }
            }
        }
    }
}