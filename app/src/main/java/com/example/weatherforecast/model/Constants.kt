package com.example.weatherforecast.model

interface Constants {
    companion object{
        val APIKEY="1e7a04e94b7995a0aa4e2725dc521e00"
        val MAPBOX_API_KEY="pk.eyJ1IjoiYWJkZWxyYWhtYW4xMTc3IiwiYSI6ImNrbGtmMnVnZTIxaGYydXByazh1MjdkMHcifQ.nYb6znVbHaDT4iXJj7q4pg"
        val logTag="test"
        val sharedPrefLocation="LocationSharedPref"
        val sharedPrefSettings="SettingsSharedPref"
        val sharedPrefFirstTime="firstTime"
        val sharedPrefLat="currentLat"
        val sharedPrefLon="currentLon"
        var unitsInput="unitsInput"
        var langInput="langInput"
        val unitsImperial="imperial" //For temperature in Fahrenheit and wind speed in miles/hour
        val unitsMetric="metric" //For temperature in Celsius and wind speed in meter/sec
        val unitsStandard="standard" //Temperature in Kelvin and wind speed in meter/sec
        val langEn="en"
        val langAr="ar"
        val langDe="de"
        val langRu="ru"
    }
}