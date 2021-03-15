package com.example.weatherforecast.datalayer.local.alarm

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.weatherforecast.model.Alert

@Dao
interface AlertDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
   fun insertAlert(alert: Alert):Long

   @Query("select * from alert")
   fun getAlertsLiveData():LiveData<List<Alert>>

   @Query("delete from alert where id= :id ")
   fun deleteAlert(id:Int)
}