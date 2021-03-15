package com.example.weatherforecast.datalayer

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.weatherforecast.R
import com.example.weatherforecast.datalayer.local.WeatherDB
import com.example.weatherforecast.datalayer.local.WeatherDao
import com.example.weatherforecast.datalayer.local.alarm.AlertDB
import com.example.weatherforecast.datalayer.local.alarm.AlertDao
import com.example.weatherforecast.datalayer.remote.WeatherApi
import com.example.weatherforecast.datalayer.remote.WeatherService
import com.example.weatherforecast.model.Alert
import com.example.weatherforecast.model.Constants
import com.example.weatherforecast.model.Current
import com.example.weatherforecast.model.Weather
import com.example.weatherforecast.utilities.Utility
import com.example.weatherforecast.view.HomeFragment
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.*

class Repository() {
    lateinit var remoteWeather: WeatherApi
    lateinit var weatherDB: WeatherDB
    lateinit var weatherDao: WeatherDao
    lateinit var alertDB: AlertDB
    lateinit var alertDao: AlertDao
    lateinit var liveCurrentWeather: MutableLiveData<Weather>

    constructor(application: Application) : this() {
        remoteWeather = WeatherService.getWeatherService()
        weatherDB = WeatherDB.getDatabase(application)
        weatherDao = weatherDB.WordDao()
        alertDB = AlertDB.getDatabase(application)
        alertDao = alertDB.alertDao()
        liveCurrentWeather = MutableLiveData()

    }

    fun insertHomeWeather(weather: Weather) {
        CoroutineScope(Dispatchers.IO).launch {
            weatherDao.insertWeather(weather)
            liveCurrentWeather.postValue(weather)
        }
    }

    fun insertFavoriteWeather(weather: Weather) {
        CoroutineScope(Dispatchers.IO).launch {
            weatherDao.insertWeather(weather)
        }
    }

    fun getFavoriteWeatherList(context: Context): LiveData<List<Weather>>? {
        val sharedPref =
            context.getSharedPreferences(Constants.sharedPrefLocation, Context.MODE_PRIVATE)
        val lat = sharedPref.getString(Constants.sharedPrefLat, null)
        val lon = sharedPref.getString(Constants.sharedPrefLon, null)
        return if (lat == null || lon == null) {
            null
        } else {
            weatherDao.getFilteredWeatherList(lat!!, lon!!)
        }
    }


    fun getUnit(context: Context): String {
        val sharedPref =
            context.getSharedPreferences(Constants.sharedPrefSettings, Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        val unit = sharedPref.getString(Constants.unitsInput, null)
        return if (unit == null || unit.equals("")) {
            editor.apply {
                putString(Constants.unitsInput, Constants.unitsStandard)
            }.apply()
            Constants.unitsStandard
        } else {
            unit
        }
    }

    fun setUnit(context: Context, unit: String) {
        val sharedPref =
            context.getSharedPreferences(Constants.sharedPrefSettings, Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        setTempUnit(context, unit)
        setSpeedUnit(context, unit)
        editor.apply {
            putString(Constants.unitsInput, unit)
        }.apply()
    }

    fun setTempUnit(context: Context, unit: String) {
        val sharedPref =
            context.getSharedPreferences(Constants.sharedPrefSettings, Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        var tempUnit = ""
        when (unit) {
            "standard" -> tempUnit = context.resources.getString(R.string.kelv)
            "metric" -> tempUnit = context.resources.getString(R.string.cel)
            "imperial" -> tempUnit = context.resources.getString(R.string.fahr)
            else -> tempUnit = ""
        }
        editor.apply {
            putString(Constants.tempUnit, tempUnit)
        }.apply()
    }

    fun setSpeedUnit(context: Context, unit: String) {
        val sharedPref =
            context.getSharedPreferences(Constants.sharedPrefSettings, Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        var speedUnit = ""
        when (unit) {
            "standard" -> speedUnit = context.resources.getString(R.string.meterPerSec)
            "metric" -> speedUnit = context.resources.getString(R.string.meterPerSec)
            "imperial" -> speedUnit = context.resources.getString(R.string.milePerHr)
            else -> speedUnit = ""
        }
        editor.apply {
            putString(Constants.speedUnit, speedUnit)
        }.apply()
    }

    fun setLocale(context: Context, locale: String) {
        val sharedPref =
            context.getSharedPreferences(Constants.sharedPrefSettings, Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.apply {
            putString(Constants.langInput, locale)
        }.apply()
    }

    fun deleteItem(lat: Double, lon: Double) {
        CoroutineScope(Dispatchers.IO).launch {
            weatherDao.deleteOldHome("" + lat, "" + lon)
        }
    }

    fun saveCurrentLocation(context: Context, currentLocation: LatLng) {
        val sharedPref =
            context.getSharedPreferences(Constants.sharedPrefLocation, Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        if (isNewLocation(context, currentLocation)) {
            val lat = sharedPref.getString(Constants.sharedPrefLat, null)
            val lon = sharedPref.getString(Constants.sharedPrefLon, null)
            CoroutineScope(Dispatchers.IO).launch {
                if (lat != null || lon != null) {
                    weatherDao.deleteOldHome(lat!!, lon!!)
                }
            }
            editor.apply {
                putString(Constants.sharedPrefLat, "" + currentLocation.latitude)
                putString(Constants.sharedPrefLon, "" + currentLocation.longitude)
            }.apply()
        }
    }


    fun isNewLocation(context: Context, newLocation: LatLng): Boolean {
        val sharedPref =
            context.getSharedPreferences(Constants.sharedPrefLocation, Context.MODE_PRIVATE)
        val lat = sharedPref.getString(Constants.sharedPrefLat, null)
        val lon = sharedPref.getString(Constants.sharedPrefLon, null)
        return if (("" + newLocation.latitude).equals(lat) && ("" + newLocation.longitude).equals(
                lon
            )
        ) {
            false
        } else {
            true
        }
    }

    fun fetchData(lat: Double, long: Double, context: Context):MutableLiveData<Weather> {
        var weather=MutableLiveData<Weather>()
        val unit = getUnit(context)
        val locale = Utility.getLocale(context)
        CoroutineScope(Dispatchers.IO).launch {
            val response = remoteWeather.getWeather(
                lat,
                long,
                "minutely",
                Constants.APIKEY,
                unit,
                locale
            )
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    weather.postValue(response.body())
                }
            }
        }
        return weather
    }

    fun getHomeWeather(context: Context, lifecycleOwner: LifecycleOwner): LiveData<Weather> {
        val sharedPref =
            context.getSharedPreferences(Constants.sharedPrefLocation, Context.MODE_PRIVATE)
        val lat = sharedPref.getString(Constants.sharedPrefLat, null)
        val lon = sharedPref.getString(Constants.sharedPrefLon, null)
        if (lat != null && lon != null) {
            weatherDao.getHomeWeather(lat!!, lon!!).observe(lifecycleOwner, {
                liveCurrentWeather.postValue(it)
            })
        }
        return liveCurrentWeather
    }

    fun insertAlert(alert: Alert): Long {
        return alertDao.insertAlert(alert)
    }

    fun getAlertsList(): LiveData<List<Alert>> {
        return alertDao.getAlertsLiveData()
    }

    fun deleteAlert(id: Int) {
        alertDao.deleteAlert(id)
    }
}