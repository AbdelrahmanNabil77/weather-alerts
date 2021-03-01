package com.example.weatherforecast.datalayer.remote

import com.google.gson.Gson
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

object WeatherService {
private val baseUrl="https://api.openweathermap.org/data/2.5/"
    fun getWeatherService():WeatherApi{
        return Retrofit.Builder().baseUrl(baseUrl).addConverterFactory(GsonConverterFactory.create()).build()
            .create(WeatherApi::class.java)
    }
//val retrofit= Retrofit.Builder().baseUrl(baseUrl).addConverterFactory(GsonConverterFactory.create()).build()
}