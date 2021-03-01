package com.example.weatherforecast.datalayer

import com.example.weatherforecast.datalayer.remote.WeatherService

class Repository {
    val remoteWeather=WeatherService.getWeatherService()
}