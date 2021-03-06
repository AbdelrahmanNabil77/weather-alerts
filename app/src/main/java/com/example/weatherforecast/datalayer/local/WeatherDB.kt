package com.example.weatherforecast.datalayer.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.weatherforecast.model.Weather

@Database(entities = arrayOf(Weather::class),version = 1,exportSchema = false)
abstract class WeatherDB:RoomDatabase() {
     abstract fun WordDao():WeatherDao
    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.

        private var INSTANCE: WeatherDB? = null

        fun getDatabase(context: Context): WeatherDB {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                var instance = Room.databaseBuilder(
                    context.applicationContext,
                    WeatherDB::class.java,
                    "weather"
                ).build()
                INSTANCE = instance
                // return instance
                return instance
            }
        }



    }
}