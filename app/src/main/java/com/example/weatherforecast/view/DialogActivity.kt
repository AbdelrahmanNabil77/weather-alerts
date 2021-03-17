package com.example.weatherforecast.view

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.DialogInterface
import android.media.MediaPlayer
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.ViewModelProvider
import com.example.weatherforecast.R
import com.example.weatherforecast.databinding.ActivityDialogBinding
import com.example.weatherforecast.model.Constants
import com.example.weatherforecast.model.Constants.Companion.channel1ID
import com.example.weatherforecast.utilities.Utility
import com.example.weatherforecast.viewmodel.AlarmViewModel
import com.example.weatherforecast.viewmodel.HomeViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DialogActivity : AppCompatActivity() {
    lateinit var alarmViewModel:AlarmViewModel
    lateinit var homeViewModel: HomeViewModel
    private lateinit var binding: ActivityDialogBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDialogBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        var id = intent.getIntExtra(Constants.alertID, 0)
        var event = intent.getStringExtra(Constants.EVENT)
        var alert = intent.getStringExtra(Constants.ALERT)

        alarmViewModel = ViewModelProvider(this).get(AlarmViewModel::class.java)
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        if (Utility.isOnline(this)) {
            alarmViewModel.getAlertWeatherLive().observe(this, {
                homeViewModel.insertHomeWeather(it)
                if (it.current?.weather?.get(0)?.description!!.equals(event)) {
                    if (alert.equals("Alarm")) {
                        showDialog(it.timezone!!, event!!)
                    } else {
                        showNotification(it.timezone!!, event!!)
                    }
                } else {
                }
            })
        }
        else{
            showInternetConnectionErrorDialog()
        }
            CoroutineScope(Dispatchers.IO).launch {
                alarmViewModel.deleteAlert(intent.getIntExtra(Constants.alertID, 0))
            }



    }

    fun showInternetConnectionErrorDialog() {
        AlertDialog.Builder(this).setTitle(R.string.networkError).setMessage(R.string.networkAlertIssue).setPositiveButton(R.string.ok) { dialog, which ->
            finish()
        }.show()
    }

    fun showDialog(location:String,event:String){
        var mp=MediaPlayer.create(this,R.raw.zaz)
        mp.isLooping=true
        mp.start()
        val dialog = AlertDialog.Builder(this)
        dialog.setCancelable(false)
        dialog.setTitle("Weather alert")
        dialog.setMessage("The weather today in $location is $event")
        dialog.setPositiveButton("ok",DialogInterface.OnClickListener { dialog, which ->
            finish()
            mp.stop()
        })
        dialog.show()
    }

    fun showNotification(location:String,event:String){
        var notificationManagerCompat= NotificationManagerCompat.from(this)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel1 = NotificationChannel(
                    Constants.channel1ID,
                    "Weather alert", NotificationManager.IMPORTANCE_HIGH
                )
                channel1.description = "The weather today in $location is $event"
                val notificationManager = getSystemService(
                    NotificationManager::class.java
                )
                notificationManager.createNotificationChannel(channel1)
            }
            val notification = NotificationCompat.Builder(this, channel1ID)
                .setSmallIcon(R.drawable.ic_warning)
                .setContentTitle("Weather alert").setContentText("The weather today in $location is $event")
                .setPriority(NotificationManager.IMPORTANCE_HIGH)
                .setAutoCancel(true)
                .setOngoing(false)
                .build()
            notificationManagerCompat.notify(1, notification)

        finish()

    }
}