package com.example.weatherforecast.datalayer.converters

import androidx.room.TypeConverter
import com.example.weatherforecast.model.Current
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class CurrentTypeConverter {
    companion object{
        @TypeConverter
        @JvmStatic
        fun fromCurrent(value: Current): String? {
            val gson = Gson()
            val type = object : TypeToken<Current>() {}.type
            return gson.toJson(value, type)
        }

        @TypeConverter
        @JvmStatic
        fun toCurrent(value: String?): Current? {
            if (value == null) {
                return null
            }
            val gson = Gson()
            val type = object : TypeToken<Current>() {}.type
            return gson.fromJson(value, type)
        }
    }
}