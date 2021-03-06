package com.example.weatherforecast.datalayer.local

import androidx.annotation.NonNull
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weatherforecast.model.Weather

@Dao
interface WeatherDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertWeather(weather: Weather)

    @Query("select * from Weather where lat= :lat AND lon= :lon ")
    fun getHomeWeather(lat:String, lon:String): LiveData<Weather>

    @Query("delete from Weather where lat= :lat AND lon= :lon ")
    fun deleteOldHome(lat:String, lon:String)

    @Query("select * from Weather where lat!= :lat AND lon!= :lon ")
    fun getFilteredWeatherList(lat:String, lon:String): LiveData<List<Weather>>

    @Query("select * from Weather")
    fun getWeatherList():LiveData<List<Weather>>
}