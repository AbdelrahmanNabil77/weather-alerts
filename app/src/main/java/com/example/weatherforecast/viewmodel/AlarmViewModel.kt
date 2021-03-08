package com.example.weatherforecast.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.weatherforecast.datalayer.Repository
import com.example.weatherforecast.model.Weather

class AlarmViewModel(var app: Application) : AndroidViewModel(app) {
    var repository: Repository



    init {
        repository = Repository(app)
    }
}