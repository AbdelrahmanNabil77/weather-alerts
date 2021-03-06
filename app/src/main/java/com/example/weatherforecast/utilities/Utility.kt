package com.example.weatherforecast.utilities

import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import com.example.weatherforecast.R
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.*
import java.util.Calendar.*
import kotlin.coroutines.coroutineContext

class Utility() {
    companion object{
        fun getIcon(context: Context?,id:String):Drawable?{
            when(id){
                "01d"->{
                    return context?.getDrawable(R.drawable.ic_01d)
                }
                "01n"->{
                    return context?.getDrawable(R.drawable.ic_01n)
                }
                "02d"->{
                    return context?.getDrawable(R.drawable.ic_02d)
                }
                "02n"->{
                    return context?.getDrawable(R.drawable.ic_02n)
                }
                "03d","03n","04d","04n"->{
                    return context?.getDrawable(R.drawable.ic_03)
                }
                "09d","10d"->{
                    return context?.getDrawable(R.drawable.ic_10d)
                }
                "09n","10n"->{
                    return context?.getDrawable(R.drawable.ic_10n)
                }
                "11d"->{
                    return context?.getDrawable(R.drawable.ic_11d)
                }
                "11n"->{
                    return context?.getDrawable(R.drawable.ic_11n)
                }
                "13d"->{
                    return context?.getDrawable(R.drawable.ic_13d)
                }
                "13n"->{
                    return context?.getDrawable(R.drawable.ic_13n)
                }
                "50d"->{
                    return context?.getDrawable(R.drawable.ic_50d)
                }
                "50n"->{
                    return context?.getDrawable(R.drawable.ic_50n)
                }
                else->{
                    return null
                }
            }
        }

        fun getCurrentDateAndTime():Calendar{
            var calendar=Calendar.getInstance()
            calendar.set(YEAR,(calendar.get(YEAR)))
            calendar.set(MONTH,(calendar.get(MONTH)))
            calendar.set(DAY_OF_MONTH,(calendar.get(DAY_OF_MONTH)))
            calendar.set(HOUR_OF_DAY,(calendar.get(HOUR_OF_DAY)))
            calendar.set(MINUTE,(calendar.get(MINUTE)))
            return calendar
        }

        fun epochToDate(epoch:Int):LocalDate{
            val dt = Instant.ofEpochSecond(epoch.toLong()).atZone(ZoneId.systemDefault()).toLocalDate()
            return dt

        }
    }
}