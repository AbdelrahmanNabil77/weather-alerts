package com.example.weatherforecast.datalayer.local.alarm

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.weatherforecast.datalayer.local.WeatherDB
import com.example.weatherforecast.model.Alert


@Database(entities = arrayOf(Alert::class),version = 1,exportSchema = false)
abstract class AlertDB: RoomDatabase() {
    abstract fun alertDao():AlertDao
    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.

        private var INSTANCE: AlertDB? = null

        fun getDatabase(context: Context): AlertDB {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                var instance = Room.databaseBuilder(
                    context.applicationContext,
                    AlertDB::class.java,
                    "alert"
                ).build()
                INSTANCE = instance
                // return instance
                return instance
            }
        }



    }
}