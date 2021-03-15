package com.example.weatherforecast.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import com.example.weatherforecast.datalayer.Repository
import com.example.weatherforecast.model.Constants
import com.example.weatherforecast.viewmodel.AlarmViewModel

class AlertReceiver: BroadcastReceiver() {
    lateinit var repository: Repository
    override fun onReceive(context: Context?, intent: Intent?) {

    }
}