package com.example.weatherforecast.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import com.example.weatherforecast.datalayer.Repository
import java.util.*

class SettingsViewModel(var app: Application) : AndroidViewModel(app) {
    var repository: Repository
    init {
        repository = Repository(app)
    }

    fun setUnit(context: Context, unit:String){
        repository.setUnit(context,unit)
    }

    fun setLocale(context: Context,locale:String){
        repository.setLocale(context, locale)
    }


}
