package com.example.weatherforecast.datalayer.remote

import com.example.weatherforecast.model.Weather
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
    @GET("onecall?")
    suspend fun getWeather(@Query("lat")lat:Double,@Query("lon")lon:Double,
    @Query("exclude")exclude:String,@Query("appid")appid:String
            ,@Query("units")units:String,@Query("lang")lang:String): Response<Weather>

}