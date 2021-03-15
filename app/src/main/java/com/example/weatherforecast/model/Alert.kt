package com.example.weatherforecast.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alert")
data class Alert (
    @PrimaryKey(autoGenerate = true) var id:Int=0,
    val year:Int,
    val month:Int,
    val day:Int,
    val hour:Int,
    val minute:Int,
    val event:String,
    val alert:String
        ){



}