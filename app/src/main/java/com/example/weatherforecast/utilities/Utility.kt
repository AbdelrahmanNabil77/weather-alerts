package com.example.weatherforecast.utilities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import com.example.weatherforecast.R
import com.example.weatherforecast.model.Constants
import java.text.SimpleDateFormat
import java.time.*
import java.time.format.DateTimeFormatter
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

        @SuppressLint("MissingPermission")
        fun isOnline(context: Context): Boolean {
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (connectivityManager != null) {
                val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
                if (capabilities != null) {
                    if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                        Log.d(Constants.logTag, "NetworkCapabilities.TRANSPORT_CELLULAR")
                        return true
                    } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                        Log.d(Constants.logTag, "NetworkCapabilities.TRANSPORT_WIFI")
                        return true
                    } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                        Log.d(Constants.logTag, "NetworkCapabilities.TRANSPORT_ETHERNET")
                        return true
                    }
                }
            }
            return false
        }

        fun epochToTime(epoch:Int):String{
            //val simpleDateFormat = SimpleDateFormat("hh:mm")
            val dateTimFormat=DateTimeFormatter.ofPattern("hh:mm")
            val dt = Instant.ofEpochSecond(epoch.toLong()).atZone(ZoneId.systemDefault()).toLocalTime()
            var time=dt.format(dateTimFormat)
            var cal=Calendar.getInstance()
            cal.set(Calendar.HOUR_OF_DAY,dt.hour)
            cal.set(Calendar.MINUTE,dt.minute)

            return time

        }

         fun getDayTime(dt:Int,timezoneOffset: Int):String{
            val calender = Calendar.getInstance()
             val cal= getInstance()
             var mins=cal.get(Calendar.MINUTE)
            calender.timeInMillis = dt.plus(timezoneOffset).minus(7200).toLong()*1000L+(mins*60000L)
            val dateFormat = SimpleDateFormat("EE, HH:MM")
            return dateFormat.format(calender.time)
        }

        fun getDate(dt:Int,timezoneOffset: Int):String{
            val calender = Calendar.getInstance()
            calender.timeInMillis = dt.plus(timezoneOffset).minus(7200).toLong()*1000L
            val dateFormat = SimpleDateFormat("dd/MM/yyyy")
            return dateFormat.format(calender.time)
        }

        fun isMorning():Boolean{
            var calendar= getInstance()
            var hours=calendar.get(HOUR_OF_DAY) as Integer
            return if(hours>=5&&hours<=17) {
                true
            }else{
                false
            }

        }

        fun getUnit(context: Context):String{
            val sharedPref= context.getSharedPreferences(Constants.sharedPrefSettings,Context.MODE_PRIVATE)
            val editor=sharedPref.edit()
            val unit=sharedPref.getString(Constants.unitsInput,null)
            return if (unit==null||unit.equals("")){
                editor.apply{
                    putString(Constants.unitsInput,Constants.unitsStandard)
                }.apply()
                Constants.unitsStandard
            }else{
                unit
            }
        }

        fun getTempUnit(context: Context):String{
            val sharedPref= context.getSharedPreferences(Constants.sharedPrefSettings,Context.MODE_PRIVATE)
            val editor=sharedPref.edit()
            val temp=sharedPref.getString(Constants.tempUnit,null)
            return if(temp==null||temp.equals("")){
                editor.apply{
                    putString(Constants.tempUnit,context.resources.getString(R.string.kelv))
                }.apply()
                context.resources.getString(R.string.kelv)
            }else{
                temp
            }
        }

        fun getWindSpeedUnit(context: Context):String{
            val sharedPref= context.getSharedPreferences(Constants.sharedPrefSettings,Context.MODE_PRIVATE)
            val editor=sharedPref.edit()
            val speed=sharedPref.getString(Constants.speedUnit,null)
           return if(speed==null||speed.equals("")){
               editor.apply{
                   putString(Constants.speedUnit,context.resources.getString(R.string.meterPerSec))
               }.apply()
               context.resources.getString(R.string.meterPerSec)
           }else{
               speed
           }
        }

        fun getLocale(context: Context):String{
            val sharedPref= context.getSharedPreferences(Constants.sharedPrefSettings,Context.MODE_PRIVATE)
            val editor=sharedPref.edit()
            val locale=sharedPref.getString(Constants.langInput,null)
            return if (locale==null||locale.equals("")){
                editor.apply{
                    putString(Constants.langInput, Constants.langEn)
                }.apply()
                Constants.langEn
            }else{
                locale
            }
        }
    }
}