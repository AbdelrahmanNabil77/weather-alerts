package com.example.weatherforecast.datalayer.converters

import androidx.room.TypeConverter
import com.example.weatherforecast.model.AlertsItem
import com.example.weatherforecast.model.HourlyItem
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class HourlyItemTypeConverter {
    companion object{
        @TypeConverter
        @JvmStatic
        fun fromHourlyItemList(value: MutableList<HourlyItem?>?): String? {
            val gson = Gson()
            val type = object : TypeToken<MutableList<HourlyItem>>() {}.type
            return gson.toJson(value, type)
        }

        @TypeConverter
        @JvmStatic
        fun toHourlyItemList(value: String?): MutableList<HourlyItem?>? {
            if (value == null) {
                return null
            }
            val gson = Gson()
            val type = object : TypeToken<MutableList<HourlyItem>>() {}.type
            return gson.fromJson(value, type)
        }
    }
}