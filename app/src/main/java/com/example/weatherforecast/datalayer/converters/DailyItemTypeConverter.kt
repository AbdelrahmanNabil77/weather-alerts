package com.example.weatherforecast.datalayer.converters

import androidx.room.TypeConverter
import com.example.weatherforecast.model.AlertsItem
import com.example.weatherforecast.model.DailyItem
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class DailyItemTypeConverter {
companion object{
    @TypeConverter
    @JvmStatic
    fun fromDailyItemList(value: MutableList<DailyItem>?): String? {
        val gson = Gson()
        val type = object : TypeToken<MutableList<DailyItem>>() {}.type
        return gson.toJson(value, type)
    }

    @TypeConverter
    @JvmStatic
    fun toDailyItemList(value: String?): MutableList<DailyItem?>? {
        if (value == null) {
            return null
        }
        val gson = Gson()
        val type = object : TypeToken<MutableList<DailyItem>>() {}.type
        return gson.fromJson(value, type)
    }
}
}