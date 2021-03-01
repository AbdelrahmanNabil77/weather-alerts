package com.example.weatherforecast.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.*
import com.example.weatherforecast.R
import com.example.weatherforecast.databinding.ActivityMainBinding
import com.example.weatherforecast.model.Constants
import java.util.*

class MainActivity : AppCompatActivity() {
    lateinit var navController: NavController
    lateinit var appBarConfiguration: AppBarConfiguration
    lateinit var binding: ActivityMainBinding
    lateinit var calendar:Calendar
    lateinit var hours:Integer
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        calendar= Calendar.getInstance()
        hours=calendar.get(Calendar.HOUR_OF_DAY) as Integer
        appBarConfiguration= AppBarConfiguration(
            setOf(R.id.homeFragment, R.id.favoriteFragment, R.id.alarmFragment),findViewById<DrawerLayout>(R.id.drawerLayout))
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer) as NavHostFragment
        navController = navHostFragment.navController
        setSupportActionBar(binding.mainToolbar)
        setupActionBarWithNavController(navController,appBarConfiguration)
        binding.navBar.setupWithNavController(navController)
        binding.sideDrawer.setupWithNavController(navController)
        if(hours>=5&&hours<=17) {
            Log.d(Constants.logTag,""+hours)
            binding.gifBG.setBackgroundResource(R.drawable.morning_bg_2)
        }else{
            binding.gifBG.setBackgroundResource(R.drawable.night_bg)
        }



    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration)||super.onSupportNavigateUp()
    }
}