package com.example.weatherforecast.viewmodel

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.weatherforecast.datalayer.Repository
import com.example.weatherforecast.model.Alert
import com.example.weatherforecast.model.Constants
import com.example.weatherforecast.model.Weather
import com.example.weatherforecast.utilities.Utility
import com.example.weatherforecast.view.DialogActivity
import java.util.*

class AlarmViewModel(var app: Application) : AndroidViewModel(app) {
    var alertWeatherLiveData=MutableLiveData<Weather>()
    var repository: Repository

    init {
        repository = Repository(app)
    }

    fun getAlertWeatherLive():MutableLiveData<Weather>{
        val sharedPref= app.getSharedPreferences(Constants.sharedPrefLocation, Context.MODE_PRIVATE)
        val lat=sharedPref.getString(Constants.sharedPrefLat,null)
        val lon=sharedPref.getString(Constants.sharedPrefLon,null)
        if(lat!=null&&lon!=null){
            repository.fetchData(lat.toDouble(),lon.toDouble(),app).observeForever {
                alertWeatherLiveData.postValue(it)
            }
        }
        return alertWeatherLiveData
    }

    fun insertAlert(alert: Alert):Long{
        return repository.insertAlert(alert)
    }

    fun getAlertsList():LiveData<List<Alert>>{
        return repository.getAlertsList()
    }

    fun deleteAlert(id:Int){
        repository.deleteAlert(id)
    }

    fun setAlarm(calendar: Calendar, alert:Alert, id:Long,context: Context) {
        var alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        var broadcastIntent = Intent(context, DialogActivity::class.java)
        broadcastIntent.flags= Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        broadcastIntent.putExtra(Constants.alertID,id.toInt())
        broadcastIntent.putExtra(Constants.ALERT, alert.alert)
        broadcastIntent.putExtra(Constants.EVENT, alert.event)
        var pendingBroadcastIntent = PendingIntent.getActivity(
            context, id.toInt(),
            broadcastIntent, 0
        )
        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingBroadcastIntent
        )
    }
    fun isValidTime(calendar: Calendar):Boolean{
        var currentTime= Utility.getCurrentDateAndTime()
        return if(currentTime.after(calendar)){
            false
        }else{
            true
        }
    }

}